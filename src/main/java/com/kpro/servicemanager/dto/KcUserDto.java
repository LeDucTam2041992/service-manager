package com.kpro.servicemanager.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class KcUserDto {
  private String id;
  private String username;
  private String firstName;
  private String lastname;
  private String email;
  private Date createdTimestamp;

}
