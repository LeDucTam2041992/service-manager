package com.kpro.servicemanager.utils;

import com.kpro.servicemanager.entity.ResourceEntity;
import org.springframework.data.jpa.domain.Specification;

public class ResourceSpecifications {
    private ResourceSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<ResourceEntity> hasResourceName(String searchKey) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("resourceName")), "%" + searchKey.toLowerCase() + "%");
    }
}
