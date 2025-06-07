package vn.quangkhongbiet.homestay_booking.domain.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentMethod;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ngày nhận phòng là bắt buộc")
    private LocalDate checkinDate;

    @NotNull(message = "Ngày trả phòng là bắt buộc")
    private LocalDate checkoutDate;

    @Min(value = 1, message = "Số lượng khách phải ít nhất là 1")
    private int guests;

    @NotNull(message = "Trạng thái đặt phòng là bắt buộc")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Min(value = 0, message = "Tổng trước phí không được âm")
    private BigDecimal subtotal;

    @Max(value = 0, message = "Giảm giá không được dương")
    private BigDecimal discount;

    @Min(value = 0, message = "Tổng tiền không được âm")
    private BigDecimal totalAmount;

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    @Column(columnDefinition = "TEXT")
    private String note;

    @NotNull(message = "Trạng thái thanh toán là bắt buộc")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Instant paymentDate;

    @NotNull(message = "Người dùng là bắt buộc")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Homestay là bắt buộc")
    @ManyToOne
    @JoinColumn(name = "homestay_id")
    private Homestay homestay;
}