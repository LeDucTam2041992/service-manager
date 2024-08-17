package com.kpro.servicemanager.dto;

import com.kpro.servicemanager.contants.Action;
import com.kpro.servicemanager.contants.PermissionStatus;
import jakarta.validation.constraints.NotEmpty;
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
public class AdminGetUserPermissionDto extends CommonDto {
    @NotEmpty
    private String code;
    private ResourceDto resource;
    private Action action;
    private String description;
    private String status = PermissionStatus.ACTIVE.name();
    private String releaseStatus;
}
