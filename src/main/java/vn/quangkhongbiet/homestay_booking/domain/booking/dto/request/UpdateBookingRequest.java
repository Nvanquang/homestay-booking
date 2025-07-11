package vn.quangkhongbiet.homestay_booking.domain.booking.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequest {
    
    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long id;

    private BookingStatus status;

    private Instant paymentDate;
}
