package vn.quangkhongbiet.homestay_booking.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditTrailListener.class)
public class Permission implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "api_path")
    private String apiPath;

    @Column(name = "method")
    private String method;

    @Column(name = "module")
    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "updated_by")
    private String updatedBy;

    @PreRemove
    private void removeFromRoles(){
        for (Role role : this.roles) {
            role.getPermissions().remove(this);
        }
    }

    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }
}
