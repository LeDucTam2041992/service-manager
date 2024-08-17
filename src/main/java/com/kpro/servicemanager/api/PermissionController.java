package com.kpro.servicemanager.api;

import com.kpro.common.dto.base.ApiResponseFactory;
import com.kpro.common.dto.base.BaseApiResponse;
import com.kpro.servicemanager.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/check")
    public ResponseEntity<BaseApiResponse<Object>> doCheckPermissions(@RequestParam String permission, @RequestParam String username) {
        var isPermissions = permissionService.doCheckPermission(permission, username);
        return ResponseEntity.ok(ApiResponseFactory.success(isPermissions));
    }
}
