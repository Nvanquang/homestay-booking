package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;
import vn.quangkhongbiet.homestay_booking.service.homestay.LocationService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;

import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/locations/{id}")
    @ApiMessage("Lấy thông tin địa điểm theo ID thành công")
    public ResponseEntity<?> findLocationById(@PathVariable("id") Long id) {
        Optional<Location> location = locationService.findLocationById(id);
        return location.isPresent()
                ? ResponseEntity.ok(location.get())
                : ResponseEntity.notFound().build();
    }
}
