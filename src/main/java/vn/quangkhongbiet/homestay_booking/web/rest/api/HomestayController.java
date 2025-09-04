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
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.SearchHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.CreateHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.HomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.UpdateHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.SearchHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Homestay", description = "Homestay management")
public class HomestayController {

    private static final String ENTITY_NAME = "homestay";

    private final HomestayService homestayService;

    @PostMapping("/homestays")
    @ApiMessage("Homestay created successfully")
    public ResponseEntity<CreateHomestayResponse> createHomestay(
            @Valid @RequestPart("homestay") CreateHomestayRequest request,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("folder") String folder) {
                
        log.info("REST request to create Homestay: {}, folder: {}, files count: {}", request, folder, files != null ? files.length : 0);

        if (files == null || files.length == 0) {
            throw new BadRequestAlertException("No files uploaded", ENTITY_NAME, "nofiles");
        }
        if (folder == null || folder.trim().isEmpty()) {
            throw new BadRequestAlertException("Folder name cannot be empty", ENTITY_NAME, "folderempty");
        }
        CreateHomestayResponse createHomestay = this.homestayService.createHomestay(request, files, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createHomestay);
    }

    @GetMapping("/homestays/{id}")
    @ApiMessage("Get homestay information successfully")
    public ResponseEntity<HomestayResponse> getHomestayById(@PathVariable("id") Long id) {
        log.info("REST request to get Homestay by id: {}", id);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(homestayService.findHomestayById(id));
    }

    @GetMapping("/homestays/search")
    @ApiMessage("Search homestay successfully")
    public ResponseEntity<List<SearchHomestayResponse>> getAllHomestays(@Valid SearchHomestayRequest request) {
        log.info("REST request to search Homestay: {}", request);
        return ResponseEntity.ok(this.homestayService.searchHomestays(request));
    }

    @GetMapping("/homestays")
    @ApiMessage("Get all homestays successfully")
    public ResponseEntity<PagedResponse> getAllHomestays(@Filter Specification<Homestay> spec,
            Pageable pageable) {
        log.info("REST request to get all Homestays, pageable: {}", pageable);
        return ResponseEntity.ok(this.homestayService.findAllHomestays(spec, pageable));
    }

    @PostMapping("/homestays/{homestayId}/amenities")
    @ApiMessage("Add amenities to homestay successfully")
    public ResponseEntity<UpdateHomestayResponse> addAmenitiesToHomestay(
            @PathVariable("homestayId") Long homestayId,
            @RequestBody Map<String, List<Long>> request) {
        log.info("REST request to add amenities to Homestay, homestayId: {}, amenities: {}", homestayId, request.get("amenities"));

        List<Long> amenityIds = request.get("amenities");
        if (amenityIds == null || amenityIds.isEmpty()) {
            throw new BadRequestAlertException("Amenities cannot be left blank", ENTITY_NAME,
                    "amenitiesempty");
        }
        UpdateHomestayResponse updatedHomestay = this.homestayService.addAmenitiesToHomestay(homestayId, amenityIds);
        return ResponseEntity.ok(updatedHomestay);
    }

    @PatchMapping("/homestays/{id}")
    @ApiMessage("Homestay updated successfully")
    public ResponseEntity<UpdateHomestayResponse> updatePartialHomestay(@PathVariable("id") Long id,
            @RequestPart("homestay") UpdateHomestayRequest dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "folder", required = false) String folder) {
        log.info("REST request to update Homestay partially, id: {}, body: {}", id, dto);

        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idmismatch");
        }
        UpdateHomestayResponse updatedHomestay = this.homestayService.updatePartialHomestay(dto, files, folder);
        return ResponseEntity.ok(updatedHomestay);
    }

    @DeleteMapping("/homestays/{id}")
    @ApiMessage("Homestay deleted successfully")
    public ResponseEntity<Void> deleteHomestay(@PathVariable("id") Long id) {
        log.info("REST request to delete Homestay by id: {}", id);

        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        this.homestayService.deleteHomestay(id);
        return ResponseEntity.ok().body(null);
    }
}
