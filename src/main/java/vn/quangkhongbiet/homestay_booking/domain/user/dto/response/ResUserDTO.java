package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private Long id;
    private String userName;
    private String email;
    private String phoneNumber;
    private String fullName;
    private Gender gender;
    private ResUserRole role;
}
