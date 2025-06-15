package vn.quangkhongbiet.homestay_booking.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditTrailListener.class)
public class Permission implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên quyền là bắt buộc")
    @Size(min = 2, max = 100, message = "Tên quyền phải có độ dài từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Đường dẫn API là bắt buộc")
    @Size(max = 255, message = "Đường dẫn API không được vượt quá 255 ký tự")
    private String apiPath;

    @NotBlank(message = "Phương thức là bắt buộc")
    @Pattern(regexp = "^(GET|POST|PATCH|DELETE)$", message = "Phương thức phải là GET, POST, PATCH hoặc DELETE")
    private String method;

    @NotBlank(message = "Module là bắt buộc")
    @Size(max = 100, message = "Module không được vượt quá 100 ký tự")
    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
