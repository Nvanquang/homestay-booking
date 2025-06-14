package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;
import vn.quangkhongbiet.homestay_booking.repository.LocationRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.LocationService;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public boolean isLocationExists(String ward, String district, String city) {
        return this.locationRepository.existsByWardAndDistrictAndCity(ward, district, city);
    }

    @Override
    public Location createLocation(Location location) {
        if (!isLocationExists(location.getWard(), location.getDistrict(), location.getCity())) {
            return locationRepository.save(location);
        }
        return locationRepository.findByWardAndDistrictAndCity(location.getWard(), location.getDistrict(), location.getCity());
    }

    @Override
    public Optional<Location> findLocationById(Long id) {
        return locationRepository.findById(id);
    }

}