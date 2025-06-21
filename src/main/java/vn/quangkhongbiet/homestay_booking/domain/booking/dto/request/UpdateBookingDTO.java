package vn.quangkhongbiet.homestay_booking.domain.booking.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingDTO {
    @NotNull(message = "ID đặt phòng là bắt buộc")
    private Long id;

    private BookingStatus status;

    private Instant paymentDate;
}
