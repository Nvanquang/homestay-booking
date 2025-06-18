package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomestayDTO {
    @NotNull(message = "ID đặt phòng là bắt buộc")
    private Long id;

    private String name;
    private String description;
    private HomestayStatus status;
    private Integer guests;
}
