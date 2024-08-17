package com.kpro.servicemanager.entity;

import com.kpro.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "user_permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted=false")
public class UserPermissionEntity extends CommonEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    private PermissionEntity permissionEntity;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_group_id")
    private PermissionGroupEntity permissionGroupEntity;

    private String username;

    @Column(name = "status", columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserPermissionEntity that = (UserPermissionEntity) o;
        return Objects.equals(username, that.username) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, status);
    }

    @Override
    public String toString() {
        return "PermissionRelationalEntity{" +
                "username='" + username + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
