package com.kpro.servicemanager.api;

import com.kpro.common.dto.base.SearchResult;
import com.kpro.common.sercurity.utils.InternalTokenUtils;
import com.kpro.servicemanager.contants.ErrorCode;
import com.kpro.servicemanager.dto.AdminGetUserPermissionDto;
import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.dto.PermissionGroupDto;
import com.kpro.servicemanager.dto.ResourceDto;
import com.kpro.servicemanager.dto.UpsertPermissionDto;
import com.kpro.servicemanager.dto.UserPermissionDto;
import com.kpro.servicemanager.exception.ServiceManagerException;
import com.kpro.servicemanager.service.KCAdminService;
import com.kpro.servicemanager.service.PermissionAdminService;
import com.kpro.servicemanager.service.PermissionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final PermissionAdminService permissionAdminService;
    private final PermissionService permissionService;
    private final InternalTokenUtils internalTokenUtils;
    private final KCAdminService kcAdminService;

    @GetMapping("/findUserByName")
    public Object findUserByName(@RequestParam("name") String name) {
        var username = internalTokenUtils.getUsernameFromAuth();
        var hasPermission = permissionService
            .doCheckPermission("Kc User Operations", "Kc User Management", "READ", username);
        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        var rs = kcAdminService.searchByUsername(name, false);
        if (rs.isEmpty()) throw new ServiceManagerException(ErrorCode.USER_NOT_FOUND);
        return rs.get(0);
    }

    @GetMapping("/permissions/users")
    public SearchResult<AdminGetUserPermissionDto> getUserPermission(@RequestParam("username") String username, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return permissionService.adminGetUserPermission(username, page, size);
    }

    @PostMapping("/permissions")
    public String createPermission(@RequestBody UpsertPermissionDto dto) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Operations", "Permission Management", "CREATE", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return permissionAdminService.createPermission(dto);
    }

    @GetMapping("/permissions/{id}")
    public String getPermission(@PathVariable("id") String id) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Operations", "Permission Management", "READ", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return "DONE";
    }

    @GetMapping("/permissions/alls")
    public SearchResult<PermissionDto> getPermissionAll(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Operations", "Permission Management", "READ", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return permissionAdminService.getAllPermission(name, page, size);
    }

    @PostMapping("/permission-groups")
    public String createPermissionGroup(@RequestBody PermissionGroupDto dto) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Group Operations", "Permission Group Management", "CREATE", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return permissionAdminService.createPermissionGroup(dto);
    }

    @PostMapping("/permission-groups/{id}/assign")
    public void assignPermissions(@PathVariable("id") UUID groupId, @RequestBody List<UUID> permissionIds) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Group Operations", "Permission Group Management", "UPDATE", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        permissionAdminService.assignPermissionGroup(groupId, permissionIds);
    }

    @PostMapping("/users/permissions")
    public void assignPermissionsUser(@RequestBody UserPermissionDto dto) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Permission Operations", "Permission Management", "APPROVAL", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        permissionAdminService.assignPermissionUser(dto);
    }

    @DeleteMapping("/users/permissions/{id}")
    public void deleteUserPermissionById(@PathVariable("id") UUID id) {
        permissionAdminService.deletePermissionUser(id);
    }


    @PostMapping("/users/permissions/deleteAllById")
    public void deletePermissionAllById(@RequestBody List<UUID> ids) {
        permissionAdminService.deletePermissionUsers(ids);
    }

    @GetMapping("/resources")
    public SearchResult<ResourceDto> getResources(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Resource Operations", "Resource Management", "READ", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return permissionAdminService.getAllResource(name, page, size);
    }

    @PostMapping("/resources")
    public UUID createResources(@RequestBody ResourceDto dto) {
//        var username = internalTokenUtils.getUsernameFromAuth();
//        var hasPermission = permissionService
//            .doCheckPermission("Resource Operations", "Resource Management", "CREATE", username);
//        if (!hasPermission) throw new ServiceManagerException(ErrorCode.ACCESS_DENIED);
        return permissionAdminService.createResource(dto);
    }
}
