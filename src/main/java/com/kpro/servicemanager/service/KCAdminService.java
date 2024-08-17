package com.kpro.servicemanager.service;

import com.kpro.servicemanager.dto.KcUserDto;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KCAdminService {
  @Value("${KC_REAL:k-product}")
  private String realm;

  @Autowired
  private Keycloak keycloak;

  public List<KcUserDto>  searchByUsername(String username, boolean exact) {
    return keycloak.realm(realm)
        .users()
        .searchByUsername(username, exact)
        .stream().map(this::toKcUserDto).toList();
  }

  private KcUserDto toKcUserDto(UserRepresentation userRepresentation) {
    var dto = new KcUserDto();
    dto.setId(userRepresentation.getId());
    dto.setUsername(userRepresentation.getUsername());
    dto.setFirstName(userRepresentation.getFirstName());
    dto.setLastname(userRepresentation.getLastName());
    dto.setEmail(userRepresentation.getEmail());
    dto.setCreatedTimestamp(new Date(userRepresentation.getCreatedTimestamp()));
    return dto;
  }
}
