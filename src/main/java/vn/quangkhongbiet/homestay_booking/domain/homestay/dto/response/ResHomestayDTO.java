package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResHomestayDTO {
    private Long id;
    private String name;
    private String description;
    private HomestayStatus status;
    private String address;
    private Integer maxGuests;
    private Location location;
    private List<String> images;
    private List<String> amenities;
}
