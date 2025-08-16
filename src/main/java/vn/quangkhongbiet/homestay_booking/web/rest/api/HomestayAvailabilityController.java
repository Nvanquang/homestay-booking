package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.CreateAvailabilityRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Tag(name = "HomestayAvailability", description = "Homestay availability management")
public class HomestayAvailabilityController {
    
    private final HomestayAvailabilityService availabilityService;

    @PostMapping("/availabilities")
    @ApiMessage("Homestay availability created successfully")
    public ResponseEntity<Void> createHomestayAvailability(@Valid @RequestBody CreateAvailabilityRequest availabilities) {
        log.info("REST request to create HomestayAvailability: {}", availabilities);
        this.availabilityService.saveAll(availabilities);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PatchMapping("/availabilities")
    @ApiMessage("Homestay availability updated successfully")
    public ResponseEntity<HomestayAvailability> updateHomestayAvailability(@Valid @RequestBody HomestayAvailability availability) {
        log.info("REST request to update HomestayAvailability: {}", availability);
        return ResponseEntity.ok(this.availabilityService.updateHomestayAvailability(availability));
    }

    @GetMapping("/availabilities")
    @ApiMessage("Get all homestay availabilities")
    public ResponseEntity<PagedResponse> getAll(@Filter Specification<HomestayAvailability> spec,
            Pageable pageable) {
        log.info("REST request to get all Homestay Availability, pageable: {}", pageable);
        return ResponseEntity.ok(this.availabilityService.findAllAvailabilities(spec, pageable));
    }
    
}
