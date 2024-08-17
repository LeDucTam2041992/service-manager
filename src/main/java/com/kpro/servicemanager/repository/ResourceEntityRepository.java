package com.kpro.servicemanager.repository;

import com.kpro.servicemanager.entity.ResourceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResourceEntityRepository extends JpaRepository<ResourceEntity, UUID>,
    JpaSpecificationExecutor<ResourceEntity> {
}
