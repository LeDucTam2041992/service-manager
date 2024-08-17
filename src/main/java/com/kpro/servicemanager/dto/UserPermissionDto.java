package com.kpro.servicemanager.dto;

import com.kpro.servicemanager.contants.PermissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class UserPermissionDto extends CommonDto {
    @NotNull
    @NotBlank
    private String username;

    private String status = PermissionStatus.ACTIVE.name();
    private List<UUID> permissionIds;
    private UUID permissionGroupId;
}
