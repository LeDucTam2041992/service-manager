package com.kpro.servicemanager.dto;

import com.kpro.servicemanager.contants.Action;
import com.kpro.servicemanager.contants.PermissionStatus;
import java.util.List;
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
public class UpsertPermissionDto extends CommonDto {
    private String code;
    private ResourceDto resource;
    private List<Action> actions;
    private String description;
    private String status = PermissionStatus.ACTIVE.name();
    private String releaseStatus;
}
