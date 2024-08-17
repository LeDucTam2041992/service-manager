package com.kpro.servicemanager.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {

  @Value("${KC_URL:http://keycloak-1705165153.default.svc.cluster.local:80}")
  private String url;

  @Value("${KC_REALM:master}")
  private String realm;

  @Value("${KC_CLIENT_ID:admin-cli}")
  private String clientId;

  @Value("${KC_USERNAME:tamld}")
  private String username;

  @Value("${KC_PASSWORD:123456}")
  private String password;

  @Bean
  Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(url)
        .realm(realm)
        .clientId(clientId)
        .grantType(OAuth2Constants.PASSWORD)
        .username(username)
        .password(password)
        .build();
  }

}
