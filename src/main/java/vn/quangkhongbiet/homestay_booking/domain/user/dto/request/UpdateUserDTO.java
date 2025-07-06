package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    @NotNull(message = "ID không được để trống")
    private Long id;

    private Gender gender;

    private String userName;

    private String fullName;

    private String phoneNumber;

    private Boolean verified;

    private String role;
}
