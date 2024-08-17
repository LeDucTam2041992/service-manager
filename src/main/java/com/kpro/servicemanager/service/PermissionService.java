package com.kpro.servicemanager.service;

import com.kpro.common.dto.base.SearchResult;
import com.kpro.servicemanager.dto.AdminGetUserPermissionDto;
import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.dto.PermissionGroupDto;

import java.util.List;

public interface PermissionService {
    boolean doCheckPermission(String permission, String username);
    boolean doCheckPermission(String resource, String function, String action, String username);
    List<String> getPermissionUser(String username);
    void cachePermissionEvict(String username);
    List<PermissionGroupDto> getAllPermissionInfoUser();

    List<PermissionDto> getAllPermissionUser();

    SearchResult<AdminGetUserPermissionDto> adminGetUserPermission(String username, Integer page, Integer size);


    SearchResult<PermissionDto> getAllPermission(String name, Integer page, Integer size);
}
