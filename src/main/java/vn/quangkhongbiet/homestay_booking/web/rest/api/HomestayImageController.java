package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HomestayImageController {

    private static final Logger log = LoggerFactory.getLogger(HomestayImageController.class);
    
    private static final String ENTITY_NAME = "HomestayImage";

    private final HomestayImageService homestayImageService;

    @PostMapping("/homestay/{homestayId}/images")
    @ApiMessage("Upload homestay images thành công")
    public ResponseEntity<List<HomestayImage>> uploadHomestayImages(
            @PathVariable("homestayId") Long homestayId,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("folder") String folder) {
        log.info("REST request to upload HomestayImage for homestayId: {}, folder: {}, files count: {}", homestayId, folder, files != null ? files.length : 0);
        if (files == null || files.length == 0) {
            throw new BadRequestAlertException("No files uploaded", ENTITY_NAME, "nofiles");
        }
        if (folder == null || folder.trim().isEmpty()) {
            throw new BadRequestAlertException("Folder name cannot be empty", ENTITY_NAME, "folderempty");
        }
        // Gọi Service để xử lý upload
        List<HomestayImage> savedImages = homestayImageService.createHomestayImages(files, homestayId, folder);
        return ResponseEntity.ok(savedImages);
    }

    @GetMapping("/homestay/{homestayId}/images")
    @ApiMessage("Lấy hình ảnh theo homestayID thành công")
    public ResponseEntity<List<HomestayImage>> findHomestayImageByHomestayId(
            @PathVariable("homestayId") Long homestayId) {
        log.info("REST request to get HomestayImage by homestayId: {}", homestayId);
        List<HomestayImage> images = homestayImageService.findHomestayImageByHomestayId(homestayId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/homestay-images/{id}")
    @ApiMessage("Xoá hình ảnh theo ID thành công")
    public ResponseEntity<Void> deleteImage(@PathVariable("id") Long id) {
        log.info("REST request to delete HomestayImage by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        homestayImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}