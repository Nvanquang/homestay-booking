package vn.quangkhongbiet.homestay_booking.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.audit.AuditTrailListener;
import vn.quangkhongbiet.homestay_booking.domain.audit.Auditable;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditTrailListener.class)
public class User implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    private String avatar;

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    @JsonProperty("fcm_token")
    private String fcmToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Column(name = "verified")
    private Boolean verified;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Homestay> homestay;

    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "updated_by")
    private String updatedBy;

    @PreRemove
    private void preRemoveCleanup(){
        if (this.role != null) {
            this.role.getUsers().remove(this);
            this.role = null;
        }

        for (Booking booking : bookings) {
            booking.setUser(null);
        }
    }

}