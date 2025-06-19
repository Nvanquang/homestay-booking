package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqHomestaySearch;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResSearchHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomestayController {

    private static final Logger log = LoggerFactory.getLogger(HomestayController.class);

    private static final String ENTITY_NAME = "homestay";

    private final HomestayService homestayService;

    @PostMapping("/homestays")
    @ApiMessage("Tạo homestay thành công")
    public ResponseEntity<ResHomestayCreateDTO> createHomestay(
            @Valid @RequestPart("homestay") Homestay homestay,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("folder") String folder) {
                
        log.info("REST request to create Homestay: {}, folder: {}, files count: {}", homestay, folder, files != null ? files.length : 0);

        if (files == null || files.length == 0) {
            throw new BadRequestAlertException("No files uploaded", ENTITY_NAME, "nofiles");
        }
        if (folder == null || folder.trim().isEmpty()) {
            throw new BadRequestAlertException("Folder name cannot be empty", ENTITY_NAME, "folderempty");
        }
        Homestay createHomestay = this.homestayService.createHomestay(homestay, files, folder);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.homestayService.convertToResCreateHomestayDTO(createHomestay));
    }

    @GetMapping("/homestays/{id}")
    @ApiMessage("Lấy thông tin homestay thành công")
    public ResponseEntity<Homestay> getHomestayById(@PathVariable("id") Long id) {
        log.info("REST request to get Homestay by id: {}", id);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(homestayService.findHomestayById(id));
    }

    @GetMapping("/homestays/search")
    @ApiMessage("Search homestay thành công")
    public ResponseEntity<List<ResSearchHomestayDTO>> getAllHomestays(@Valid ReqHomestaySearch request) {
        log.info("REST request to search Homestay: {}", request);
        return ResponseEntity.ok(this.homestayService.searchHomestays(request));
    }

    @GetMapping("/homestays")
    @ApiMessage("Lấy tất cả homestay thành công")
    public ResponseEntity<PagedResponse> getAllHomestays(@Filter Specification<Homestay> spec,
            Pageable pageable) {
        log.info("REST request to get all Homestays, pageable: {}", pageable);
        return ResponseEntity.ok(this.homestayService.findAllHomestays(spec, pageable));
    }

    @PostMapping("/homestays/{homestayId}/amenities")
    @ApiMessage("Thêm tiện nghi cho homestay thành công")
    public ResponseEntity<?> addAmenitiesToHomestay(
            @PathVariable("homestayId") Long homestayId,
            @RequestBody Map<String, List<Long>> request) {
        log.info("REST request to add amenities to Homestay, homestayId: {}, amenities: {}", homestayId, request.get("amenities"));

        List<Long> amenityIds = request.get("amenities");
        if (amenityIds == null || amenityIds.isEmpty()) {
            throw new BadRequestAlertException("Amenities cannot be left blank", ENTITY_NAME,
                    "amenitiesempty");
        }
        Homestay updatedHomestay = this.homestayService.addAmenitiesToHomestay(homestayId, amenityIds);
        return ResponseEntity.ok(this.homestayService.convertToResUpdatedHomestayDTO(updatedHomestay));
    }

    @PatchMapping("/homestays/{id}")
    @ApiMessage("Cập nhật homestay thành công")
    public ResponseEntity<ResHomestayUpdatedDTO> updatePartialHomestay(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateHomestayDTO dto) {
        log.info("REST request to update Homestay partially, id: {}, body: {}", id, dto);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idmismatch");
        }
        Homestay updatedHomestay = this.homestayService.updatePartialHomestay(dto);
        return ResponseEntity.ok(this.homestayService.convertToResUpdatedHomestayDTO(updatedHomestay));
    }

    @DeleteMapping("/homestays/{id}")
    @ApiMessage("Xóa homestay thành công")
    public ResponseEntity<Void> deleteHomestay(@PathVariable("id") Long id) {
        log.info("REST request to delete Homestay by id: {}", id);

        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        homestayService.deleteHomestay(id);
        return ResponseEntity.noContent().build();
    }
}
