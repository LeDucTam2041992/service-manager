package com.kpro.servicemanager.mapper;

import com.kpro.servicemanager.dto.ResourceDto;
import com.kpro.servicemanager.entity.ResourceEntity;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

  public ResourceDto toResourceDto(ResourceEntity resource) {
    var dto = new ResourceDto();
    dto.setId(resource.getId());
    dto.setResourceCode(resource.getResourceCode());
    dto.setResourceName(resource.getResourceName());
    dto.setFunctionCode(resource.getFunctionCode());
    dto.setFunctionName(resource.getFunctionName());
    dto.setStatus(resource.getStatus());
    dto.setId(resource.getId());
    dto.setCreatedAt(resource.getCreatedAt());
    dto.setCreatedBy(resource.getCreatedBy());
    dto.setUpdatedAt(resource.getUpdatedAt());
    return dto;
  }

  public ResourceEntity toEntity(ResourceDto dto) {
    return this.toEntity(dto, new ResourceEntity());
  }

  private ResourceEntity toEntity(ResourceDto dto, ResourceEntity entity) {
    entity.setResourceCode(dto.getResourceCode());
    entity.setResourceName(dto.getResourceName());
    entity.setFunctionCode(dto.getFunctionCode());
    entity.setFunctionName(dto.getFunctionName());
    entity.setStatus(dto.getStatus());
    return entity;
  }
}
