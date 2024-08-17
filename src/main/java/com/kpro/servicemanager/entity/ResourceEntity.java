package com.kpro.servicemanager.entity;

import com.kpro.common.entity.CommonEntity;
import com.kpro.servicemanager.contants.ResourceStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "resource")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted=false")
public class ResourceEntity extends CommonEntity {
  private String resourceName;
  private String resourceCode;
  private String functionName;
  private String functionCode;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private ResourceStatus status;

  @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<PermissionEntity> permissionEntities;
}
