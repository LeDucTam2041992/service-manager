package com.kpro.servicemanager.service;

import com.kpro.common.dto.base.SearchResult;
import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.dto.PermissionGroupDto;
import com.kpro.servicemanager.dto.ResourceDto;
import com.kpro.servicemanager.dto.UpsertPermissionDto;
import com.kpro.servicemanager.dto.UserPermissionDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface PermissionAdminService {

  String createPermission(@Valid UpsertPermissionDto dto);

  String createPermissionGroup(PermissionGroupDto dto);

  void assignPermissionGroup(UUID groupId, List<UUID> permissionIds);

  void assignPermissionUser(UserPermissionDto dto);

  List<PermissionDto> getAllPermissionUser();

  SearchResult<ResourceDto> getAllResource(String name, Integer page, Integer size);

  UUID createResource(ResourceDto dto);

  SearchResult<PermissionDto> getAllPermission(String name, Integer page, Integer size);

  void deletePermissionUsers(List<UUID> ids);

  void deletePermissionUser(UUID id);
}
