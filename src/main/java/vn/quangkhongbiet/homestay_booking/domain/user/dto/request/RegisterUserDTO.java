package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.validation.RegisterChecked;

@Getter
@Setter
@RegisterChecked
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String userName;
    private String password;
    private String confirmPassword;

    @Email(message = "Email không hợp lệ!", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    private String phoneNumber;
    private String fullName;
    private Gender gender;
}
