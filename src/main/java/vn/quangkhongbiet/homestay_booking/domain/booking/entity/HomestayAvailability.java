package vn.quangkhongbiet.homestay_booking.domain.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "homestay_availability")
@IdClass(HomestayAvailabilityId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomestayAvailability {

    @Id
    @Column(name = "homestay_id")
    private Long homestayId;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AvailabilityStatus status;
}