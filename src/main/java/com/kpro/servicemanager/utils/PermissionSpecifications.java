package com.kpro.servicemanager.utils;

import com.kpro.servicemanager.entity.PermissionEntity;
import org.springframework.data.jpa.domain.Specification;

public class PermissionSpecifications {
    private PermissionSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<PermissionEntity> hasResourceName(String searchKey) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("resource").get("resourceName")), "%" + searchKey.toLowerCase() + "%");
    }
}
