package com.kpro.servicemanager.dto;

import com.kpro.servicemanager.contants.ResourceStatus;
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
public class ResourceDto extends CommonDto {
  private String resourceName;
  private String resourceCode;
  private String functionName;
  private String functionCode;
  private ResourceStatus status;
}
