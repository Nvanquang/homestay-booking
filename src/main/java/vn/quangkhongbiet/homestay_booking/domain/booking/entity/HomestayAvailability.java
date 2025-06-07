package vn.quangkhongbiet.homestay_booking.domain.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "homestay_availability")
@IdClass(HomestayAvailabilityId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomestayAvailability {

    @Id
    private Long homestayId;

    @Id
    private LocalDate date;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status;
}