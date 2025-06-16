package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import java.util.List;

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
public class ResRoleDTO {
    private Long id;
    private String name;
    private Boolean active;
    private String description;
    private List<ResRolePermission> permissions;
}
