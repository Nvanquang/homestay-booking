package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    
    @NotNull(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    
    @NotNull(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
    private String otp;
}
