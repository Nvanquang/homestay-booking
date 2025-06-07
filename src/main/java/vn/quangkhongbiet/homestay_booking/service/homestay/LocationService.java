package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.List;
import java.util.Optional;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;

public interface LocationService {
    Location createLocation(Location location);

    Optional<Location> findLocationById(Long id);

    List<Location> findAllLocations();

    Location updateLocation(Location location);

    void deleteLocation(Long id);
}
