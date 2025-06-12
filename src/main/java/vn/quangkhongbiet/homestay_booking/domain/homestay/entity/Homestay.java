package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "homestays")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Homestay {
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
    private Integer maxGuests;

    @NotNull(message = "Số điện thoại của homestay là bắt buộc")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    // @NotNull
    // private Double longitude;

    // @NotNull
    // private Double latitude;

    // private Point geom;

    // @NotNull(message = "Vị trí là bắt buộc")
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "homestay", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "homestay" })
    private List<HomestayImage> images;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "homestay_amenity",
        joinColumns = @JoinColumn(name = "homestay_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @JsonIgnoreProperties(value = { "homestays" })
    private List<Amenity> amenities;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
