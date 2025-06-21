package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResHomestayDTO {
    private Long id;
    private String name;
    private String description;
    private HomestayStatus status;
    private String address;
    private Integer guests;
    private Double longitude;
    private Double latitude;
    private List<String> images;
    private List<String> amenities;
}
