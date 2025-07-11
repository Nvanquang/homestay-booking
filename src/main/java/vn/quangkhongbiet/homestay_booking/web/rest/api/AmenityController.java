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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create amenity", description = "Create a new amenity in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully", content = @Content()),
        @ApiResponse(responseCode = "409", description = "Data already exists", content = @Content())
    })
    public ResponseEntity<Amenity> createAmenity(@Valid @RequestBody CreateAmenityRequest request) {
        log.info("REST request to create Amenity: {}", request);
        Amenity savedAmenity = amenityService.createAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAmenity);
    }

    @GetMapping("/amenities/{id}")
    @ApiMessage("Get amenity by ID successfully")
    @Operation(summary = "Get amenity by ID", description = "Return amenity by specific ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Amenity found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Amenity not found", content = @Content())
    })
    public ResponseEntity<Amenity> findAmenityById(@PathVariable("id") Long id) {
        log.info("REST request to get Amenity by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(amenityService.findAmenityById(id));
    }

    @GetMapping("/amenities")
    @ApiMessage("Get all amenities successfully")
    @Operation(summary = "Get amenity list", description = "Return paginated and filtered amenity list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<PagedResponse> findAllAmenities(
            @Filter Specification<Amenity> spec, Pageable pageable) {
        log.info("REST request to get all Amenities, pageable: {}", pageable);
        PagedResponse result = amenityService.findAllAmenities(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/amenities/{id}")
    @ApiMessage("Amenity deleted successfully")
    @Operation(summary = "Delete amenity", description = "Delete amenity by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Amenity not found", content = @Content())
    })
    public ResponseEntity<Void> deleteAmenity(@PathVariable("id") Long id) {
        log.info("REST request to delete Amenity by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
