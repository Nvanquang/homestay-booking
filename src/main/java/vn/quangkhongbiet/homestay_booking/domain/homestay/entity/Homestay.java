package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

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

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private HomestayStatus status;

    @Column(name = "guests")
    private Integer guests;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

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

    @OneToMany(mappedBy = "homestay")
    @JsonIgnore
    private List<Review> reviews;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "host_id")
    private User host;

    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "updated_by")
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
