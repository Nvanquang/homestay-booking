package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomestayController {

    private static final String ENTITY_NAME = "homestay";

    private final HomestayService homestayService;
    private final HomestayImageService homestayImageService;

    @PostMapping("/homestays")
    @ApiMessage("Tạo homestay thành công")
    public ResponseEntity<ResHomestayCreateDTO> createHomestay(
        @Valid @RequestPart("homestay") Homestay homestay,
        @RequestPart("files") MultipartFile[] files,
        @RequestPart("folder") String folder) {
        if (homestay == null) {
            throw new BadRequestAlertException("Dữ liệu homestay không được null", ENTITY_NAME, "homestaynotfound");
        }
        if (homestay.getAmenities() == null || homestay.getAmenities().isEmpty()) {
            throw new BadRequestAlertException("Homestay phải có ít nhất một tiện nghi", ENTITY_NAME, "amenitiesnotfound");
        }
        if (homestay.getLocation() == null) {
            throw new BadRequestAlertException("Homestay phải có vị trí", ENTITY_NAME, "locationnotfound");
        }
        
        Homestay createHomestay = this.homestayService.createHomestay(homestay, files, folder);
        createHomestay.setImages(this.homestayImageService.createHomestayImages(files, createHomestay.getId(), folder));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.homestayService.convertToResCreateHomestayDTO(createHomestay));
    }

    @GetMapping("/homestays/{id}")
    @ApiMessage("Lấy thông tin homestay thành công")
    public ResponseEntity<Homestay> getHomestayById(@PathVariable("id") Long id) {
        if (id == null) {
            throw new BadRequestAlertException("ID homestay không được null", ENTITY_NAME, "idnull");
        }
        if (!homestayService.existsById(id)) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy homestay với ID " + id, ENTITY_NAME, "homestaynotfound");
        }
        return ResponseEntity.ok(homestayService.findHomestayById(id).get());
    }

    @GetMapping("/homestays")
    @ApiMessage("Lấy tất cả homestay thành công")
    public ResponseEntity<ResultPaginationDTO> getAllHomestays(@Filter Specification<Homestay> spec, Pageable pageable) {
        return ResponseEntity.ok(this.homestayService.findAllHomestays(spec, pageable));
    }

    @PatchMapping("/homestays/{homestayId}/amenities")
    @ApiMessage("Thêm tiện nghi cho homestay thành công")
    public ResponseEntity<?> addAmenitiesToHomestay(
        @PathVariable("homestayId") Long homestayId,
        @RequestBody Map<String, List<Long>> request) {
        if (homestayId == null) {
            throw new BadRequestAlertException("ID homestay không được null", ENTITY_NAME, "idnull");
        }
        Homestay updatedHomestay = this.homestayService.addAmenitiesToHomestay(homestayId, request.get("amenities"));
        return ResponseEntity.ok(this.homestayService.convertToResUpdatedHomestayDTO(updatedHomestay));
    }

    @PatchMapping("/homestays")
    @ApiMessage("Cập nhật homestay thành công")
    public ResponseEntity<ResHomestayUpdatedDTO> updatePartialHomestay(@Valid @RequestBody Homestay homestay) {
        if (homestay.getId() == null) {
            throw new BadRequestAlertException("ID homestay không được null", ENTITY_NAME, "idnull");
        }
        if (!homestayService.existsById(homestay.getId())) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy homestay với ID " + homestay.getId(), ENTITY_NAME, "homestaynotfound");
        }
        Homestay updatedHomestay = this.homestayService.updatePartialHomestay(homestay).get();
        return ResponseEntity.ok(this.homestayService.convertToResUpdatedHomestayDTO(updatedHomestay));
    }

    @DeleteMapping("/homestays/{id}")
    @ApiMessage("Xóa homestay thành công")
    public ResponseEntity<Void> deleteHomestay(@PathVariable("id") Long id) {
        if (id == null) {
            throw new BadRequestAlertException("ID homestay không được null", ENTITY_NAME, "idnull");
        }
        if (!homestayService.existsById(id)) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy homestay với ID " + id, ENTITY_NAME, "homestaynotfound");
        }
        homestayService.deleteHomestay(id);
        return ResponseEntity.noContent().build();
    }
}
