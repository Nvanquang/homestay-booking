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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Amenity", description = "Quản lý tiện nghi homestay")
public class AmenityController {

    private static final String ENTITY_NAME = "Amenity";

    private final AmenityService amenityService;

    @PostMapping("/amenities")
    @ApiMessage("Tạo tiện nghi thành công")
    @Operation(summary = "Tạo tiện nghi", description = "Tạo mới một tiện nghi trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
    public ResponseEntity<Amenity> createAmenity(@Valid @RequestBody Amenity amenity) {
        log.info("REST request to create Amenity: {}", amenity);
        Amenity savedAmenity = amenityService.createAmenity(amenity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAmenity);
    }

    @GetMapping("/amenities/{id}")
    @ApiMessage("Lấy tiện nghi theo ID thành công")
    @Operation(summary = "Lấy tiện nghi theo ID", description = "Trả về tiện nghi theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy tiện nghi"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tiện nghi")
    })
    public ResponseEntity<Amenity> findAmenityById(@PathVariable("id") Long id) {
        log.info("REST request to get Amenity by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(amenityService.findAmenityById(id));
    }

    @GetMapping("/amenities")
    @ApiMessage("Lấy tất cả tiện nghi thành công")
    @Operation(summary = "Lấy danh sách tiện nghi", description = "Trả về danh sách tiện nghi có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> findAllAmenities(
            @Filter Specification<Amenity> spec, Pageable pageable) {
        log.info("REST request to get all Amenities, pageable: {}", pageable);
        PagedResponse result = amenityService.findAllAmenities(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/amenities/{id}")
    @ApiMessage("Xoá tiện nghi theo ID thành công")
    @Operation(summary = "Xoá tiện nghi", description = "Xoá tiện nghi theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xoá thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy tiện nghi")
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
