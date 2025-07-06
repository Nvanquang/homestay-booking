package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Tag(name = "HomestayAvailability", description = "Homestay availability management")
public class HomestayAvailabilityController {
    
    private final HomestayAvailabilityService availabilityService;

    @PostMapping("/availabilities")
    @ApiMessage("Homestay availability created successfully")
    @Operation(summary = "Create homestay availability", description = "Create a new list of homestay availabilities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    public ResponseEntity<HomestayAvailability> createHomestayAvailability(@Valid @RequestBody List<HomestayAvailability> availabilities) {
        log.info("REST request to create HomestayAvailability: {}", availabilities);
        availabilityService.saveAll(availabilities);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
    
}
