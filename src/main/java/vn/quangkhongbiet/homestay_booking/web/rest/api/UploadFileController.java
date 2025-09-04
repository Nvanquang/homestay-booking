package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.service.homestay.UploadFileService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "UplaodFile", description = "Upload management")
public class UploadFileController {

    private static final String ENTITY_NAME = "HomestayImage";

    private final UploadFileService uploadFileService;

    @PostMapping("/files/homestay/{homestayId}/images")
    @ApiMessage("Upload homestay images successfully")
    public ResponseEntity<List<HomestayImage>> uploadHomestayImages(
            @PathVariable("homestayId") Long homestayId,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("folder") String folder) {
        log.info("REST request to upload HomestayImage for homestayId: {}, folder: {}, files count: {}", homestayId,
                folder, files != null ? files.length : 0);
        if (files == null || files.length == 0) {
            throw new BadRequestAlertException("No files uploaded", ENTITY_NAME, "nofiles");
        }
        if (folder == null || folder.trim().isEmpty()) {
            throw new BadRequestAlertException("Folder name cannot be empty", ENTITY_NAME, "folderempty");
        }
        // Gọi Service để xử lý upload
        this.uploadFileService.createHomestayImages(files, homestayId, folder);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/files")
    @ApiMessage("Upload single successfully")
    public ResponseEntity<String> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder) {

        log.info("REST request to upload file to folder: {}", folder);

        if (file.isEmpty() || file == null) {
            throw new BadRequestAlertException("File is empty. Please upload the file again.", ENTITY_NAME, "fileempty");
        }
        if (folder == null || folder.trim().isEmpty()) {
            throw new BadRequestAlertException("Folder name cannot be empty", ENTITY_NAME, "folderempty");
        }

        this.uploadFileService.createDirectory(folder);
        String fileUrl = this.uploadFileService.store(file, folder);
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/files/homestay/{homestayId}/images")
    @ApiMessage("Get images by homestay ID successfully")
    public ResponseEntity<List<HomestayImage>> findHomestayImageByHomestayId(
            @PathVariable("homestayId") Long homestayId) {
        log.info("REST request to get HomestayImage by homestayId: {}", homestayId);
        List<HomestayImage> images = this.uploadFileService.findHomestayImageByHomestayId(homestayId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/files/homestay-images/{id}")
    @ApiMessage("Delete image by ID successfully")
    public ResponseEntity<Void> deleteImage(@PathVariable("id") Long id) {
        log.info("REST request to delete HomestayImage by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        this.uploadFileService.deleteImage(id);
        return ResponseEntity.ok().body(null);
    }
}