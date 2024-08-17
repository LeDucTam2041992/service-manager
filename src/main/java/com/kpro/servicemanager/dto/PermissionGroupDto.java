package com.kpro.servicemanager.dto;

import com.kpro.servicemanager.contants.PermissionStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class PermissionGroupDto extends CommonDto {
    private String code;
    private String description;
    private String status = PermissionStatus.ACTIVE.name();
    private List<PermissionDto> permissionDtoList = new ArrayList<>();
}
