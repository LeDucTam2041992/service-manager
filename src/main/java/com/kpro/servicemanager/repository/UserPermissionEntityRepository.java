package com.kpro.servicemanager.repository;

import com.kpro.servicemanager.entity.UserPermissionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPermissionEntityRepository extends JpaRepository<UserPermissionEntity, UUID> {

  @Query(value = "select p from UserPermissionEntity p where p.username = :username "
      + "and p.status = 'ACTIVE'")
  List<UserPermissionEntity> findAllByUsernameAndStatusActive(@Param("username") String username);

  @Query(value = "select p from UserPermissionEntity p where p.username = :username")
  Page<UserPermissionEntity> findAllByUsername(@Param("username") String username, PageRequest pageRequest);
}
