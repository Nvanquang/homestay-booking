package vn.quangkhongbiet.homestay_booking.web.rest.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.tags.Tag;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateAmenityRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Amenity", description = "Amenity management")
public class AmenityController {

    private static final String ENTITY_NAME = "Amenity";

    private final AmenityService amenityService;

    @PostMapping("/amenities")
    @ApiMessage("Amenity created successfully")
    public ResponseEntity<Amenity> createAmenity(@Valid @RequestBody CreateAmenityRequest request) {
        log.info("REST request to create Amenity: {}", request);
        Amenity savedAmenity = amenityService.createAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAmenity);
    }

    @GetMapping("/amenities/{id}")
    @ApiMessage("Get amenity by ID successfully")
    public ResponseEntity<Amenity> findAmenityById(@PathVariable("id") Long id) {
        log.info("REST request to get Amenity by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(amenityService.findAmenityById(id));
    }

    @GetMapping("/amenities")
    @ApiMessage("Get all amenities successfully")
    public ResponseEntity<PagedResponse> findAllAmenities(
            @Filter Specification<Amenity> spec, Pageable pageable) {
        log.info("REST request to get all Amenities, pageable: {}", pageable);
        PagedResponse result = amenityService.findAllAmenities(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/amenities/{id}")
    @ApiMessage("Amenity deleted successfully")
    public ResponseEntity<Void> deleteAmenity(@PathVariable("id") Long id) {
        log.info("REST request to delete Amenity by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        amenityService.deleteAmenity(id);
        return ResponseEntity.ok().body(null);
    }
}
