package com.kpro.servicemanager.dto;

import java.time.LocalDateTime;
import java.util.Date;
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
public class CommonDto {
  private UUID id;
  private boolean isDeleted;
  private LocalDateTime deletedAt;
  private Date createdAt;
  private Date updatedAt;
  private boolean isTraveledTime;
  private String createdBy;
}
