package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {
    @NotNull(message = "Id canbe not null")
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private List<Long> permissions;
}
