package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;
import vn.quangkhongbiet.homestay_booking.repository.LocationRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.LocationService;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);
    
    private final LocationRepository locationRepository;

    @Override
    public boolean isLocationExists(String ward, String district, String city) {
        log.debug("check Location exists by ward: {}, district: {}, city: {}", ward, district, city);
        return this.locationRepository.existsByWardAndDistrictAndCity(ward, district, city);
    }

    @Override
    public Location createLocation(Location location) {
        log.debug("create Location with location: {}", location);
        if (!isLocationExists(location.getWard(), location.getDistrict(), location.getCity())) {
            return locationRepository.save(location);
        }
        return locationRepository.findByWardAndDistrictAndCity(location.getWard(), location.getDistrict(), location.getCity());
    }

    @Override
    public Location findLocationById(Long id) {
        log.debug("find Location by id: {}", id);
        return locationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException (
                "Location not found with id!",
                "Location",
                "idnotfound"));
    }

}