package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "homestays")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditTrailListener.class)
public class Homestay implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên homestay là bắt buộc")
    @Size(min = 2, max = 100, message = "Tên homestay phải có độ dài từ 2 đến 100 ký tự")
    private String name;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Trạng thái homestay là bắt buộc")
    @Enumerated(EnumType.STRING)
    private HomestayStatus status;

    @Min(value = 1, message = "Số khách tối đa phải ít nhất là 1")
    private Integer guests;

    @NotNull(message = "Số điện thoại của homestay là bắt buộc")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @OneToMany(mappedBy = "homestay", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "homestay" })
    private List<HomestayImage> images;

    @NotNull(message = "Homestay phải có ít nhất một tiện nghi")
    @NotEmpty(message = "Danh sách tiện nghi không được để trống")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "homestay_amenity",
        joinColumns = @JoinColumn(name = "homestay_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @JsonIgnoreProperties(value = { "homestays" })
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "homestay")
    @JsonIgnore
    private List<Review> reviews;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    @PreRemove
    private void preRemoveCleanup() {
        for (Amenity a : amenities) {
            a.getHomestays().remove(this);
        }
        amenities.clear();

        for (Review r : reviews) {
            r.setHomestay(null);
        }
    }
}
