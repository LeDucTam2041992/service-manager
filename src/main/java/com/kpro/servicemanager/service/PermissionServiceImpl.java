package com.kpro.servicemanager.service;

import com.kpro.common.dto.base.SearchResult;
import com.kpro.common.entity.SpecificationUtil;
import com.kpro.common.sercurity.utils.InternalTokenUtils;
import com.kpro.servicemanager.config.CommonCacheConfiguration;
import com.kpro.servicemanager.contants.PermissionStatus;
import com.kpro.servicemanager.contants.ResourceStatus;
import com.kpro.servicemanager.dto.AdminGetUserPermissionDto;
import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.dto.PermissionGroupDto;
import com.kpro.servicemanager.entity.PermissionEntity;
import com.kpro.servicemanager.entity.PermissionGroupEntity;
import com.kpro.servicemanager.entity.UserPermissionEntity;
import com.kpro.servicemanager.mapper.ResourceMapper;
import com.kpro.servicemanager.repository.PermissionEntityRepository;
import com.kpro.servicemanager.repository.PermissionGroupEntityRepository;
import com.kpro.servicemanager.repository.ResourceEntityRepository;
import com.kpro.servicemanager.repository.UserPermissionEntityRepository;
import com.kpro.servicemanager.utils.PermissionSpecifications;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionEntityRepository permissionEntityRepository;
  private final PermissionGroupEntityRepository permissionGroupEntityRepository;
  private final UserPermissionEntityRepository userPermissionEntityRepository;
  private final ResourceEntityRepository resourceEntityRepository;
  private final ApplicationContext context;
  private final InternalTokenUtils internalTokenUtils;
  private final ResourceMapper resourceMapper;

  @Override
  public boolean doCheckPermission(String permission, String username) {
    log.info("{} do check permission [{}] of user [{}]", getClass().getSimpleName(), permission,
        username);
    var self = context.getBean(PermissionService.class);
    var permissionList = self.getPermissionUser(username);
    return permissionList.contains(permission);
  }

  @Override
  public boolean doCheckPermission(String resource, String function, String action,
      String username) {
    var permission = String.join("_", resource, function, action);
    return this.doCheckPermission(permission, username);
  }

  @Override
  @Cacheable(cacheNames = CommonCacheConfiguration.USER_PERMISSION, keyGenerator = "customKeyGenerator")
  public List<String> getPermissionUser(String username) {
    if (username == null) {
      return new ArrayList<>();
    }
    List<UserPermissionEntity> userPermissionEntities = userPermissionEntityRepository
        .findAllByUsernameAndStatusActive(username);
    List<String> result = new ArrayList<>();
    userPermissionEntities.stream()
        .filter(r -> r.getUsername() != null && PermissionStatus.ACTIVE.name()
            .equalsIgnoreCase(r.getStatus()))
        .forEach(userPermissionEntity -> {
          var permissionEntity = userPermissionEntity.getPermissionEntity();
          if (permissionEntity != null) {
            var permissionString = this.toPermissionString(permissionEntity);
            if (permissionString != null) {
              result.add(permissionString);
            }
          }

          var permissionGroupEntity = userPermissionEntity.getPermissionGroupEntity();
          if (permissionGroupEntity != null) {
            var permissions = permissionGroupEntity.getPermissionEntities().stream()
                .filter(p -> PermissionStatus.ACTIVE == p.getStatus())
                .map(this::toPermissionString)
                .filter(Objects::nonNull)
                .toList();
            result.addAll(permissions);
          }
        });
    return result;
  }

  private String toPermissionString(PermissionEntity permissionEntity) {
    var resource = permissionEntity.getResource();
    if (resource == null || resource.getStatus() == ResourceStatus.INACTIVE) {
      return null;
    }
    return String.join("_", resource.getResourceName(),
        resource.getFunctionName(),
        permissionEntity.getAction().toString());
  }

  @Override
  @CacheEvict(cacheNames = CommonCacheConfiguration.USER_PERMISSION, allEntries = true)
  public void cachePermissionEvict(String username) {
    //to-do
  }

  @Override
  @Cacheable(cacheNames = CommonCacheConfiguration.USER_PERMISSION_GROUP_INFO, keyGenerator = "customKeyGenerator")
  public List<PermissionGroupDto> getAllPermissionInfoUser() {
    String username = internalTokenUtils.getUsernameFromAuth();
    List<UserPermissionEntity> relationalEntityList = userPermissionEntityRepository
        .findAllByUsernameAndStatusActive(username);
    var result = new ArrayList<PermissionGroupDto>();
    relationalEntityList.stream()
        .filter(r -> r.getUsername() != null && PermissionStatus.ACTIVE.name()
            .equalsIgnoreCase(r.getStatus()))
        .forEach(userPermissionEntity -> {
          var permissionEntity = userPermissionEntity.getPermissionEntity();
          this.handlePermissionDto(result, permissionEntity);

          var permissionGroupEntity = userPermissionEntity.getPermissionGroupEntity();
          if (permissionGroupEntity != null) {
            var groupDto = this.mapToPermissionGroupDto(permissionGroupEntity);
            if (!checkDuplicateGroup(result, groupDto)) {
              result.add(groupDto);
            }
          }
        });

    return result;
  }

  @Override
  @Cacheable(cacheNames = CommonCacheConfiguration.USER_PERMISSION_INFO, keyGenerator = "customKeyGenerator")
  public List<PermissionDto> getAllPermissionUser() {
    String username = internalTokenUtils.getUsernameFromAuth();
    List<UserPermissionEntity> userPermissionEntities = userPermissionEntityRepository
        .findAllByUsernameAndStatusActive(username);
    return this.getPermissionDtos(userPermissionEntities);
  }

  @Override
  public SearchResult<AdminGetUserPermissionDto> adminGetUserPermission(String username, Integer page, Integer size) {
    Sort.Order order = new Order(Direction.DESC, "updatedAt");
    var pageRequest = PageRequest.of(page, size, Sort.by(order));
    var pageRes = userPermissionEntityRepository.findAllByUsername(username, pageRequest);
    return SearchResult.<AdminGetUserPermissionDto>builder()
        .content(this.toAdminGetUserPermission(pageRes.getContent()))
        .first(pageRes.isFirst())
        .last(pageRes.isLast())
        .size(size)
        .numberOfElements(pageRes.getNumberOfElements())
        .totalPages(pageRes.getTotalPages())
        .totalElements(pageRes.getTotalElements())
        .build();
  }

  private List<AdminGetUserPermissionDto> toAdminGetUserPermission(List<UserPermissionEntity> content) {
    var result = new ArrayList<AdminGetUserPermissionDto>();
    content.stream()
        .filter(r -> r.getUsername() != null)
        .forEach(userPermissionEntity -> {
          var permissionEntity = userPermissionEntity.getPermissionEntity();
          result.add(this.mapToAdminGetUserPermissionDto(permissionEntity, userPermissionEntity));
          var permissionGroupEntity = userPermissionEntity.getPermissionGroupEntity();
          if (permissionGroupEntity != null) {
            var permissionDtoList = permissionGroupEntity.getPermissionEntities()
                .stream()
                .map(p -> this.mapToAdminGetUserPermissionDto(p, userPermissionEntity))
                .toList();
            result.addAll(permissionDtoList);
          }
        });

    return result;
  }

  private AdminGetUserPermissionDto mapToAdminGetUserPermissionDto(PermissionEntity entity, UserPermissionEntity userPermissionEntity) {
    return AdminGetUserPermissionDto
        .builder()
        .id(userPermissionEntity.getId())
        .code(entity.getCode())
        .action(entity.getAction())
        .resource(resourceMapper.toResourceDto(entity.getResource()))
        .description(entity.getDescription())
        .releaseStatus(entity.getReleaseStatus())
        .status(userPermissionEntity.getStatus())
        .createdAt(userPermissionEntity.getCreatedAt())
        .createdBy(userPermissionEntity.getCreatedBy())
        .build();
  }

  private ArrayList<PermissionDto> getPermissionDtos(
      List<UserPermissionEntity> userPermissionEntities) {
    var result = new ArrayList<PermissionDto>();
    userPermissionEntities.stream()
        .filter(r -> r.getUsername() != null && PermissionStatus.ACTIVE.name()
            .equalsIgnoreCase(r.getStatus()))
        .forEach(userPermissionEntity -> {
          var permissionEntity = userPermissionEntity.getPermissionEntity();
          if (permissionEntity.getResource().getStatus() == ResourceStatus.ACTIVE) {
            result.add(this.mapToPermissionDto(permissionEntity));
          }
          var permissionGroupEntity = userPermissionEntity.getPermissionGroupEntity();
          if (permissionGroupEntity != null) {
            var permissionDtoList = permissionGroupEntity.getPermissionEntities()
                .stream()
                .filter(p -> p.getResource().getStatus() == ResourceStatus.ACTIVE)
                .map(this::mapToPermissionDto)
                .toList();
            result.addAll(permissionDtoList);
          }
        });

    return result;
  }

  @Override
  public SearchResult<PermissionDto> getAllPermission(String name, Integer page, Integer size) {
    Sort.Order order = new Order(Direction.DESC, "updatedAt");
    var pageRequest = PageRequest.of(page, size, Sort.by(order));
    List<Specification<PermissionEntity>> specifications = new ArrayList<>();
    if (name != null) {
      specifications.add(PermissionSpecifications.hasResourceName(name));
    }

    Specification<PermissionEntity> specs = SpecificationUtil.buildANDSpecification(specifications);
    var pageRes = permissionEntityRepository.findAll(specs, pageRequest);
    return SearchResult.<PermissionDto>builder()
        .content(pageRes.getContent().stream().map(this::mapToPermissionDto).toList())
        .first(pageRes.isFirst())
        .last(pageRes.isLast())
        .size(size)
        .numberOfElements(pageRes.getNumberOfElements())
        .totalPages(pageRes.getTotalPages())
        .totalElements(pageRes.getTotalElements())
        .build();
  }

  private void handlePermissionDto(ArrayList<PermissionGroupDto> result,
      PermissionEntity permissionEntity) {
    if (permissionEntity != null) {
      var groupEntityParent = permissionEntity.getPermissionGroupEntity();
      var groupDto = this.mapToPermissionGroupDto(groupEntityParent, permissionEntity);
      if (!checkDuplicateGroup(result, groupDto)) {
        result.add(groupDto);
      } else {
        this.groupPermissionDto(result, groupDto);
      }
    }
  }

  private void groupPermissionDto(ArrayList<PermissionGroupDto> result,
      PermissionGroupDto groupDto) {
    var dto = groupDto.getPermissionDtoList().get(0);
    for (PermissionGroupDto permissionGroupDto : result) {
      if (permissionGroupDto.getCode().equals(groupDto.getCode())) {
        var permissionDtoList = permissionGroupDto.getPermissionDtoList();
        if (permissionDtoList.stream().noneMatch(p -> p.getCode().equals(dto.getCode()))) {
          permissionDtoList.add(dto);
        }
        break;
      }
    }
  }

  private boolean checkDuplicateGroup(ArrayList<PermissionGroupDto> groupDtos,
      PermissionGroupDto groupDto) {
    return groupDtos.stream()
        .anyMatch(permissionGroupDto -> permissionGroupDto.getCode().equals(groupDto.getCode()));
  }

  @SuppressWarnings("unused")
  private boolean checkDuplicatePermissionDto(ArrayList<PermissionDto> permissionDtos,
      PermissionDto dto) {
    return permissionDtos.stream()
        .anyMatch(permissionDto -> permissionDto.getCode().equals(dto.getCode()));
  }

  private PermissionGroupDto mapToPermissionGroupDto(PermissionGroupEntity entity) {
    return PermissionGroupDto
        .builder()
        .status(entity.getStatus())
        .code(entity.getCode())
        .description(entity.getDescription())
        .permissionDtoList(
            entity.getPermissionEntities().stream().map(this::mapToPermissionDto).toList())
        .build();
  }

  private PermissionDto mapToPermissionDto(PermissionEntity entity) {
    return PermissionDto
        .builder()
        .id(entity.getId())
        .code(entity.getCode())
        .action(entity.getAction())
        .resource(resourceMapper.toResourceDto(entity.getResource()))
        .description(entity.getDescription())
        .releaseStatus(entity.getReleaseStatus())
        .status(entity.getStatus().name())
        .createdAt(entity.getCreatedAt())
        .createdBy(entity.getCreatedBy())
        .build();
  }

  private PermissionGroupDto mapToPermissionGroupDto(PermissionGroupEntity groupEntity,
      PermissionEntity permissionEntity) {
    return PermissionGroupDto
        .builder()
        .status(groupEntity.getStatus())
        .code(groupEntity.getCode())
        .description(groupEntity.getDescription())
        .permissionDtoList(Collections.singletonList(this.mapToPermissionDto(permissionEntity)))
        .build();
  }
}