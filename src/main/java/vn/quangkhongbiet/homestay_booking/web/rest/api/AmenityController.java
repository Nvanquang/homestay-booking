package vn.quangkhongbiet.homestay_booking.web.rest.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AmenityController {
    private static final String ENTITY_NAME = "Amenity";

    private final AmenityService amenityService;

    @PostMapping("/amenities")
    @ApiMessage("Tạo tiện nghi thành công")
    public ResponseEntity<Amenity> createAmenity(@Valid @RequestBody Amenity amenity) {
        Amenity savedAmenity = amenityService.createAmenity(amenity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAmenity);
    }

    @GetMapping("/amenities/{id}")
    @ApiMessage("Lấy tiện nghi theo ID thành công")
    public ResponseEntity<Amenity> findAmenityById(@PathVariable("id") Long id) {

        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(amenityService.findAmenityById(id));
    }

    @GetMapping("/amenities")
    @ApiMessage("Lấy tất cả tiện nghi thành công")
    public ResponseEntity<ResultPaginationDTO> findAllAmenities(@Filter Specification<Amenity> spec, Pageable pageable) {
        ResultPaginationDTO result = amenityService.findAllAmenities(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/amenities/{id}")
    @ApiMessage("Xoá tiện nghi theo ID thành công")
    public ResponseEntity<Void> deleteAmenity(@PathVariable("id") Long id) {
        
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
        
    }
}
