package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionDTO {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
