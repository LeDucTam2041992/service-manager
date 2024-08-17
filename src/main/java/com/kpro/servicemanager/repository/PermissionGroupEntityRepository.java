package com.kpro.servicemanager.repository;

import com.kpro.servicemanager.entity.PermissionGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionGroupEntityRepository extends JpaRepository<PermissionGroupEntity, UUID> {
}
