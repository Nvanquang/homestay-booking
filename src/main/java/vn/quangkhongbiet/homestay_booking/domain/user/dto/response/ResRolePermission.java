package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResRolePermission {
    private String apiPath;
    private String method;
    private String module;
}
