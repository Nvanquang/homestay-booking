package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpPayload {

    private String email;

    private String fullName;
    
    private String otp;
}
