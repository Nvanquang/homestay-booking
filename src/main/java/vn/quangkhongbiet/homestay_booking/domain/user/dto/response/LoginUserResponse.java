package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {
	
    private String access_token;
    private InformationUser user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class InformationUser {
        private Long id;
        private String name;
        private String email;
        @JsonIgnoreProperties("permissions")
        private Role role;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserGetAccount{
        private InformationUser user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserInsideToken{
        private Long id;
        private String name;
        private String email;
    }
}
