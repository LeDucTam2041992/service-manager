package com.kpro.servicemanager.repository;

import com.kpro.servicemanager.entity.PermissionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionEntityRepository extends JpaRepository<PermissionEntity, UUID>,
    JpaSpecificationExecutor<PermissionEntity> {
}
