package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class HomestayAvailabilityController {

    private final HomestayAvailabilityService availabilityService;
    private final HomestayService homestayService;

    @PostMapping("/availabilities")
    public ResponseEntity<?> createHomestayAvailability(@Valid @RequestBody HomestayAvailability availability) {
        if (availability == null) {
            throw new BadRequestAlertException("Phòng trống không được null", "HomestayAvailability", "homestayavailabilitynull");
        }   
        HomestayAvailability createdAvailability = availabilityService.createAvailability(availability);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAvailability);
    }

    @GetMapping("/availabilities/{id}")
    public ResponseEntity<?> getAvailabilityById(@PathVariable("id") HomestayAvailabilityId id) {
        if (id == null) {
            throw new BadRequestAlertException("HomestayAvailabilityId không được null", "HomestayAvailability", "idnull");
        } 
        if(!this.homestayService.existsById(id.getHomestayId())){
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy Homestay với ID " + id, "HomestayAvailability", "homestaynotfound");
        }
        return ResponseEntity.ok(this.availabilityService.findById(id));
    }
    

    @GetMapping("/availabilities")
    public String getAll(@RequestParam String param) {
        return new String();
    }
    
}
