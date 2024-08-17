package com.kpro.servicemanager.service;

import com.kpro.common.dto.base.SearchResult;
import com.kpro.common.entity.SpecificationUtil;
import com.kpro.common.sercurity.utils.InternalTokenUtils;
import com.kpro.servicemanager.contants.ErrorCode;
import com.kpro.servicemanager.contants.PermissionStatus;
import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.dto.PermissionGroupDto;
import com.kpro.servicemanager.dto.ResourceDto;
import com.kpro.servicemanager.dto.UpsertPermissionDto;
import com.kpro.servicemanager.dto.UserPermissionDto;
import com.kpro.servicemanager.entity.PermissionEntity;
import com.kpro.servicemanager.entity.PermissionGroupEntity;
import com.kpro.servicemanager.entity.ResourceEntity;
import com.kpro.servicemanager.entity.UserPermissionEntity;
import com.kpro.servicemanager.exception.ServiceManagerException;
import com.kpro.servicemanager.mapper.ResourceMapper;
import com.kpro.servicemanager.repository.PermissionEntityRepository;
import com.kpro.servicemanager.repository.PermissionGroupEntityRepository;
import com.kpro.servicemanager.repository.ResourceEntityRepository;
import com.kpro.servicemanager.repository.UserPermissionEntityRepository;
import com.kpro.servicemanager.utils.ResourceSpecifications;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class PermissionAdminServiceImpl implements PermissionAdminService {

  private final PermissionEntityRepository permissionEntityRepository;
  private final PermissionGroupEntityRepository permissionGroupEntityRepository;
  private final UserPermissionEntityRepository userPermissionEntityRepository;
  private final ResourceEntityRepository resourceEntityRepository;
  private final ModelMapper mapper;
  private final InternalTokenUtils internalTokenUtils;
  private final PermissionService permissionService;
  private final ResourceMapper resourceMapper;

  @Override
  public String createPermission(@Valid UpsertPermissionDto dto) {
    var entities = this.toPermissionEntities(dto);
    permissionEntityRepository.saveAll(entities);
    return "Success";
  }

  @SuppressWarnings("DuplicatedCode")
  private List<PermissionEntity> toPermissionEntities(UpsertPermissionDto dto) {
    return dto.getActions().stream().map(action -> {
      var entity = new PermissionEntity();
      if (dto.getCode() == null) {
        var code = this.autoGenCode(dto, action.name());
        entity.setCode(code);
      } else {
        entity.setCode(dto.getCode());
      }
      entity.setAction(action);
      entity.setDescription(dto.getDescription());
      entity.setStatus(PermissionStatus.valueOf(dto.getStatus()));
      entity.setReleaseStatus(dto.getReleaseStatus());
      if (dto.getResource() != null) {
        var resource = resourceEntityRepository.findById(dto.getResource().getId()).orElseThrow(
            () -> new ServiceManagerException(ErrorCode.RESOURCE_NOT_FOUND)
        );
        entity.setResource(resource);
      }
      return entity;
    }).toList();
  }

  private String autoGenCode(UpsertPermissionDto dto, String action) {
    var rsNameAcr = Arrays.stream(dto.getResource().getResourceName()
        .split("\\s+"))
        .map(s -> String.valueOf(s.charAt(0)))
        .collect(Collectors.joining());
    var fcNameAcr = Arrays.stream(dto.getResource().getFunctionName()
        .split("\\s+"))
        .map(s -> String.valueOf(s.charAt(0)))
        .collect(Collectors.joining());
    var epochTime = String.valueOf(new Date().getTime());
    return String.join("_", rsNameAcr, fcNameAcr, action, epochTime).toUpperCase(Locale.ROOT);
  }

  @SuppressWarnings("DuplicatedCode")
  private PermissionEntity toPermissionEntity(PermissionDto dto) {
    var entity = new PermissionEntity();
    entity.setCode(dto.getCode());
    entity.setAction(dto.getAction());
    entity.setDescription(dto.getDescription());
    entity.setStatus(PermissionStatus.valueOf(dto.getStatus()));
    entity.setReleaseStatus(dto.getReleaseStatus());
    if (dto.getResource() != null) {
      var resource = resourceEntityRepository.findById(dto.getResource().getId()).orElseThrow(
          () -> new ServiceManagerException(ErrorCode.RESOURCE_NOT_FOUND)
      );
      entity.setResource(resource);
    }
    return entity;
  }

  @Override
  public String createPermissionGroup(PermissionGroupDto dto) {
    var entity = new PermissionGroupEntity();
    entity.setCode(dto.getCode());
    entity.setDescription(dto.getDescription());
    entity.setStatus(dto.getStatus());

    if (dto.getPermissionDtoList() != null && !dto.getPermissionDtoList().isEmpty()) {
      var permissionList = dto.getPermissionDtoList().stream()
          .map(this::toPermissionEntity)
          .collect(Collectors.toSet());
      entity.setPermissionEntities(permissionList);
    }
    permissionGroupEntityRepository.save(entity);
    return entity.getId().toString();
  }

  @Override
  public void assignPermissionGroup(UUID groupId, List<UUID> permissionIds) {
    var groupEntityOptional = permissionGroupEntityRepository.findById(groupId);
    if (groupEntityOptional.isEmpty()) {
      throw new ServiceManagerException(ErrorCode.GROUP_PERMISSION_NOT_FOUND);
    }
    var permissionEntitySet = permissionIds.stream()
        .map(permissionEntityRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    var permissionGroupEntity = groupEntityOptional.get();
    permissionGroupEntity.getPermissionEntities().addAll(permissionEntitySet);
    permissionEntitySet.forEach(
        permissionEntity -> permissionEntity.setPermissionGroupEntity(permissionGroupEntity));
    permissionGroupEntityRepository.save(permissionGroupEntity);
  }

  @Override
  public void assignPermissionUser(UserPermissionDto dto) {
    if (dto.getPermissionIds() == null && dto.getPermissionGroupId() == null) {
      throw new ServiceManagerException(ErrorCode.ASSIGN_USER_PERMISSION_INVALID);
    }

    dto.getPermissionIds().forEach(permissionId -> {
      if (permissionId != null) {
        var optionalPermissionEntity = permissionEntityRepository.findById(permissionId);
        if (optionalPermissionEntity.isEmpty()) {
          throw new ServiceManagerException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        var permissionEntity = optionalPermissionEntity.get();
        UserPermissionEntity userPermissionEntity = new UserPermissionEntity();
        userPermissionEntity.setUsername(dto.getUsername());
        userPermissionEntity.setStatus(PermissionStatus.ACTIVE.name());
        userPermissionEntity.setPermissionEntity(permissionEntity);
        userPermissionEntityRepository.save(userPermissionEntity);
      }
    });

    var permissionGroupId = dto.getPermissionGroupId();
    if (permissionGroupId != null) {
      var optionalPermissionGroupEntity = permissionGroupEntityRepository
          .findById(permissionGroupId);
      if (optionalPermissionGroupEntity.isEmpty()) {
        throw new ServiceManagerException(ErrorCode.GROUP_PERMISSION_NOT_FOUND);
      }
      var permissionGroupEntity = optionalPermissionGroupEntity.get();
      UserPermissionEntity userPermissionEntity = new UserPermissionEntity();
      userPermissionEntity.setUsername(dto.getUsername());
      userPermissionEntity.setPermissionGroupEntity(permissionGroupEntity);
      userPermissionEntityRepository.save(userPermissionEntity);
    }
  }

  @Override
  public List<PermissionDto> getAllPermissionUser() {
    return permissionService.getAllPermissionUser();
  }

  @Override
  public SearchResult<ResourceDto> getAllResource(String name, Integer page, Integer size) {
    Sort.Order order = new Order(Direction.DESC, "updatedAt");
    var pageRequest = PageRequest.of(page, size, Sort.by(order));

    List<Specification<ResourceEntity>> specifications = new ArrayList<>();
    if (name != null) {
      specifications.add(ResourceSpecifications.hasResourceName(name));
    }

    Specification<ResourceEntity> specs = SpecificationUtil.buildANDSpecification(specifications);
    var pageRes = resourceEntityRepository.findAll(specs, pageRequest);
    return SearchResult.<ResourceDto>builder()
        .content(pageRes.getContent().stream().map(resourceMapper::toResourceDto).toList())
        .first(pageRes.isFirst())
        .last(pageRes.isLast())
        .size(size)
        .numberOfElements(pageRes.getNumberOfElements())
        .totalPages(pageRes.getTotalPages())
        .totalElements(pageRes.getTotalElements())
        .build();
  }

  @Override
  public UUID createResource(ResourceDto dto) {
    var entity = resourceMapper.toEntity(dto);
    resourceEntityRepository.save(entity);
    return entity.getId();
  }

  @Override
  public SearchResult<PermissionDto> getAllPermission(String name, Integer page, Integer size) {
    return permissionService.getAllPermission(name, page, size);
  }

  @Override
  public void deletePermissionUsers(List<UUID> ids) {
    userPermissionEntityRepository.deleteAllById(ids);
  }

  @Override
  public void deletePermissionUser(UUID id) {
    userPermissionEntityRepository.deleteById(id);
  }
}
