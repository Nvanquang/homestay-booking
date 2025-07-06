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
import lombok.extern.slf4j.Slf4j;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Homestay", description = "Quản lý homestay")
public class HomestayController {

    private static final String ENTITY_NAME = "homestay";

    private final HomestayService homestayService;

    @PostMapping("/homestays")
    @ApiMessage("Tạo homestay thành công")
    @Operation(summary = "Tạo homestay", description = "Tạo mới một homestay trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
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
        ResHomestayCreateDTO createHomestay = this.homestayService.createHomestay(homestay, files, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createHomestay);
    }

    @GetMapping("/homestays/{id}")
    @ApiMessage("Lấy thông tin homestay thành công")
    @Operation(summary = "Lấy homestay theo ID", description = "Trả về homestay theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy homestay"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy homestay")
    })
    public ResponseEntity<Homestay> getHomestayById(@PathVariable("id") Long id) {
        log.info("REST request to get Homestay by id: {}", id);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(homestayService.findHomestayById(id));
    }

    @GetMapping("/homestays/search")
    @ApiMessage("Search homestay thành công")
    @Operation(summary = "Tìm kiếm homestay", description = "Tìm kiếm homestay theo điều kiện")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Thông tin tìm kiếm không hợp lệ")
    })
    public ResponseEntity<List<ResSearchHomestayDTO>> getAllHomestays(@Valid ReqHomestaySearch request) {
        log.info("REST request to search Homestay: {}", request);
        return ResponseEntity.ok(this.homestayService.searchHomestays(request));
    }

    @GetMapping("/homestays")
    @ApiMessage("Lấy tất cả homestay thành công")
    @Operation(summary = "Lấy danh sách homestay", description = "Trả về danh sách homestay có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllHomestays(@Filter Specification<Homestay> spec,
            Pageable pageable) {
        log.info("REST request to get all Homestays, pageable: {}", pageable);
        return ResponseEntity.ok(this.homestayService.findAllHomestays(spec, pageable));
    }

    @PostMapping("/homestays/{homestayId}/amenities")
    @ApiMessage("Thêm tiện nghi cho homestay thành công")
    @Operation(summary = "Thêm tiện nghi cho homestay", description = "Thêm tiện nghi vào homestay theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<ResHomestayUpdatedDTO> addAmenitiesToHomestay(
            @PathVariable("homestayId") Long homestayId,
            @RequestBody Map<String, List<Long>> request) {
        log.info("REST request to add amenities to Homestay, homestayId: {}, amenities: {}", homestayId, request.get("amenities"));

        List<Long> amenityIds = request.get("amenities");
        if (amenityIds == null || amenityIds.isEmpty()) {
            throw new BadRequestAlertException("Amenities cannot be left blank", ENTITY_NAME,
                    "amenitiesempty");
        }
        ResHomestayUpdatedDTO updatedHomestay = this.homestayService.addAmenitiesToHomestay(homestayId, amenityIds);
        return ResponseEntity.ok(updatedHomestay);
    }

    @PatchMapping("/homestays/{id}")
    @ApiMessage("Cập nhật homestay thành công")
    @Operation(summary = "Cập nhật homestay", description = "Cập nhật thông tin homestay theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy homestay"),
        @ApiResponse(responseCode = "500", description = "Không thể cập nhật homestay")
    })
    public ResponseEntity<ResHomestayUpdatedDTO> updatePartialHomestay(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateHomestayDTO dto) {
        log.info("REST request to update Homestay partially, id: {}, body: {}", id, dto);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idmismatch");
        }
        ResHomestayUpdatedDTO updatedHomestay = this.homestayService.updatePartialHomestay(dto);
        return ResponseEntity.ok(updatedHomestay);
    }

    @DeleteMapping("/homestays/{id}")
    @ApiMessage("Xóa homestay thành công")
    @Operation(summary = "Xóa homestay", description = "Xóa homestay theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy homestay")
    })
    public ResponseEntity<Void> deleteHomestay(@PathVariable("id") Long id) {
        log.info("REST request to delete Homestay by id: {}", id);

        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        homestayService.deleteHomestay(id);
        return ResponseEntity.noContent().build();
    }
}
