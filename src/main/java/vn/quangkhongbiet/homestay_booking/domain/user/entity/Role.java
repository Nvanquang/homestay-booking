package vn.quangkhongbiet.homestay_booking.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;


@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên vai trò là bắt buộc")
    @Size(min = 2, max = 50, message = "Tên vai trò phải có độ dài từ 2 đến 50 ký tự")
    @Column(unique = true)
    private String name;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;

    @NotNull(message = "Trạng thái hoạt động là bắt buộc")
    private boolean active;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "permission_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @JsonIgnore
    private List<Permission> permissions;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

}