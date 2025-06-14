package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomestayImageController {

    private static final String ENTITY_NAME = "HomestayImage";

    private final HomestayImageService homestayImageService;

    @PostMapping("/homestay-images")
    @ApiMessage("Upload homestay images thành công")
    public ResponseEntity<?> uploadHomestayImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("homestayId") Long homestayId,
            @RequestParam("folder") String folder) {
        try {
            // Kiểm tra homestayId có null không
            if (homestayId == null) {
                throw new BadRequestAlertException(
                        "Homestay ID cannot be null",
                        ENTITY_NAME,
                        "homestayidnull"
                );
            }

            // Gọi Service để xử lý upload
            List<HomestayImage> savedImages = homestayImageService.createHomestayImages(files, homestayId, folder);
            return ResponseEntity.ok(savedImages);

        } catch (BadRequestAlertException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "entity", e.getEntityName(),
                    "errorKey", e.getErrorKey(),
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/homestay-images/{homestayId}")
    @ApiMessage("Lấy hình ảnh theo homestayID thành công")
    public ResponseEntity<?> findHomestayImageByHomestayId(@PathVariable("homestayId") long homestayId) {
        List<HomestayImage> images = homestayImageService.findHomestayImageByHomestayId(homestayId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/homestay-images/{id}")
    @ApiMessage("Xoá hình ảnh theo ID thành công")
    public ResponseEntity<?> deleteImage(@PathVariable("id") Long id) {
        if(id == null || id <= 0) {
            throw new BadRequestAlertException("ID phải dương hoặc không được null", ENTITY_NAME, "idnotpositive");
        }
        homestayImageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
}