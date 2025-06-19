package vn.quangkhongbiet.homestay_booking.domain.booking.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentMethod;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentStatus;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResBookingDTO {

    private Long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer guests;
    private BookingStatus status;
    private BigDecimal subtotal;
    private BigDecimal fee;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String note;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private Instant paymentDate;
    private ResUser user;
    private ResHomestay homestay;
}
