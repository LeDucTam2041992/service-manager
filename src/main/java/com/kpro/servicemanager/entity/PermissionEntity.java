package com.kpro.servicemanager.entity;

import com.kpro.common.entity.CommonEntity;
import com.kpro.servicemanager.contants.Action;
import com.kpro.servicemanager.contants.PermissionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "permission")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted=false")
public class PermissionEntity extends CommonEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "description")
    private String description;

    @Column(name = "status", columnDefinition = "varchar(20) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private PermissionStatus status;

    @Column(name = "release_status")
    private String releaseStatus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_group_id")
    private PermissionGroupEntity permissionGroupEntity;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private ResourceEntity resource;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionEntity)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PermissionEntity that = (PermissionEntity) o;
        return Objects.equals(getCode(), that.getCode()) && Objects
            .equals(getResource(), that.getResource()) && getAction() == that.getAction()
            && Objects.equals(getDescription(), that.getDescription()) && Objects
            .equals(getStatus(), that.getStatus()) && Objects
            .equals(getReleaseStatus(), that.getReleaseStatus());
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(super.hashCode(), getCode(), getResource(), getAction(), getDescription(),
                getStatus(), getReleaseStatus());
    }
}
