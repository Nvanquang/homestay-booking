package vn.quangkhongbiet.homestay_booking.domain.booking.dto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.utils.VnpayUtil;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateBookingRequest {

    private String requestId = VnpayUtil.getRandomNumber(8);

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Homestay ID is required")
    @Positive(message = "Homestay ID must be positive")
    private Long homestayId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkoutDate;

    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be positive")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer guests;

    @Length(max = 1000, message = "Note cannot exceed 1000 characters")
    private String note;

    private String ipAddress;
}
