package vn.quangkhongbiet.homestay_booking.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditTrailListener.class)
public class Role implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "permission_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;

    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "updated_by")
    private String updatedBy;

    @PreRemove
    private void removeFromPermissions(){
        for (Permission permission : this.permissions) {
            permission.getRoles().remove(this);
        }
        for (User user : this.users){
            user.setRole(null);
        }
    }

}