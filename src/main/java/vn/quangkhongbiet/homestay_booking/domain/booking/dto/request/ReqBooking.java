package vn.quangkhongbiet.homestay_booking.domain.booking.dto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReqBooking {
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;

    @NotNull(message = "user_id cannot be null")
    @Positive(message = "user_id must be positive")
    private Long userId;

    @NotNull(message = "homestay_id cannot be blank")
    @Positive(message = "homestay_id must be positive")
    private Long homestayId;

    @NotNull(message = "checkin_date cannot be blank")
    private LocalDate checkinDate;

    @NotNull(message = "checkout_date cannot be blank")
    private LocalDate checkoutDate;

    @Positive(message = "guests must be positive")
    @Min(value = 1, message = "guests must be at least 1")
    private Integer guests;

    @Length(max = 500, message = "note cannot be longer than 255 characters")
    private String note;
}
