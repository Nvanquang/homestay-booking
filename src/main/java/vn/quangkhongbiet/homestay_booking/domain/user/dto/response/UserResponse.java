package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import java.time.Instant;

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
public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private String phoneNumber;
    private String fullName;
    private Gender gender;
    private ResRole role;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResRole {
        private Long id;
        private String name;
    }
}
