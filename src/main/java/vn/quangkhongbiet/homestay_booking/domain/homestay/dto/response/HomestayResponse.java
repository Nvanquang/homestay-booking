package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HomestayResponse {
    private Long id;
    private String name;
    private String description;
    private HomestayStatus status;
    private String address;
    private Integer guests;
    private String phoneNumber;
    private Double longitude;
    private Double latitude;
    private HostInfo host;
    private Integer totalReviews;
    private Double averageRating;
    private List<String> images;
    private List<Amenity> amenities;

    @Getter
    @Setter
    @AllArgsConstructor
    @SuperBuilder
    public static class HostInfo {
        private Long id;
        private String name;
        private String avatar;
    }
}
