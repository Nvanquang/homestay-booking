package vn.quangkhongbiet.homestay_booking.service.homestay;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;

public interface LocationService {
    boolean isLocationExists(String ward, String district, String city);

    Location createLocation(Location location);

    Location findLocationById(Long id);
}
