package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class HomestayAvailabilityController {

    private static final Logger log = LoggerFactory.getLogger(HomestayAvailabilityController.class);
    
    private final HomestayAvailabilityService availabilityService;

    @PostMapping("/availabilities")
    @ApiMessage("Tạo phòng trống thành công")
    public ResponseEntity<HomestayAvailability> createHomestayAvailability(@Valid @RequestBody List<HomestayAvailability> availabilities) {
        log.info("REST request to create HomestayAvailability: {}", availabilities);
        availabilityService.saveAll(availabilities);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
    
}
