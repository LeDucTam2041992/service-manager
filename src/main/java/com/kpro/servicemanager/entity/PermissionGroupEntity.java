package com.kpro.servicemanager.entity;

import com.kpro.common.entity.CommonEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "permission_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted=false")
public class PermissionGroupEntity extends CommonEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "status", columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status;

    @OneToMany(mappedBy = "permissionGroupEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PermissionEntity> permissionEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionGroupEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PermissionGroupEntity that = (PermissionGroupEntity) o;
        return Objects.equals(getCode(), that.getCode()) && Objects
            .equals(getDescription(), that.getDescription()) && Objects
            .equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCode(), getDescription(), getStatus());
    }
}
