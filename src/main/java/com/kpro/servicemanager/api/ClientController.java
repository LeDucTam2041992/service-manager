package com.kpro.servicemanager.api;

import com.kpro.servicemanager.dto.PermissionDto;
import com.kpro.servicemanager.service.PermissionAdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientController {
    private final PermissionAdminService permissionAdminService;

    @GetMapping("/permissions/users")
    public List<PermissionDto> getPermission() {
        return permissionAdminService.getAllPermissionUser();
    }

}
