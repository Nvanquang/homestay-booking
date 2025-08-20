package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.repository.HomestayImageRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomestayImageServiceImpl implements HomestayImageService {

    private static final String ENTITY_NAME = "HomestayImage";

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB

    private static final int MAX_FILES = 6;

    private static final Pattern FOLDER_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private final HomestayImageRepository homestayImageRepository;

    private final HomestayRepository homestayRepository;

    private final Cloudinary cloudinary;

    @Override
    @Async
    public void createHomestayImages(MultipartFile[] files, Long homestayId, String folder) {
        log.debug("create HomestayImage with files: {}, homestayId: {}, folder: {}", files != null ? files.length : 0,
                homestayId, folder);
        validateFile(files);
        validateHomestayIdAndFolder(homestayId, folder);

        try {
            File[] fileContents = new File[files.length];
            for (int i = 0; i < files.length; i++) {
                String originalName = files[i].getOriginalFilename();
                String prefix = "upload-";
                String suffix = originalName != null ? originalName : ".tmp";

                File tempFile = File.createTempFile(prefix, suffix);
                files[i].transferTo(tempFile);
                fileContents[i] = tempFile;
            }

            this.uploadFilesAsync(fileContents, homestayId, folder);
            Thread.sleep(1000); // Đợi một chút để đảm bảo upload hoàn tất
        } catch (IOException e) {
            throw new BadRequestAlertException("Failed to convert files", ENTITY_NAME, "fileconversionfailed");
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Async
    @SneakyThrows
    public void uploadFilesAsync(File[] files, Long homestayId, String folder) {
        log.debug("uploadFilesAsync with files: {}, homestayId: {}, folder: {}", files != null ? files.length : 0,
                homestayId, folder);

        Homestay homestayDB = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Homestay not found with ID: " + homestayId, ENTITY_NAME, "homestaynotfound"));

        try {
            List<HomestayImage> savedImages = new ArrayList<>();
            for (File file : files) {
                try {
                    // Upload lên Cloudinary
                    Map<?, ?> uploadResult = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"));

                    // Tạo và lưu HomestayImage
                    HomestayImage image = new HomestayImage();
                    image.setImageUrl(uploadResult.get("secure_url").toString());
                    image.setPublicId(uploadResult.get("public_id").toString());
                    image.setHomestay(homestayDB);
                    savedImages.add(homestayImageRepository.save(image));

                } catch (IOException e) {
                    log.debug(
                            "Failed to upload file: " + file.getName(),
                            ENTITY_NAME,
                            "uploadfailed");
                } finally {
                    // Xóa file tạm sau khi upload
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

            homestayDB.setImages(savedImages);
            homestayRepository.save(homestayDB);
        } catch (Exception e) {
            log.error("Error uploading files asynchronously", e);
        }
    }

    void validateHomestayIdAndFolder(Long homestayId, String folder) {
        // Kiểm tra homestayId
        if (!homestayRepository.existsById(homestayId)) {
            throw new EntityNotFoundException(
                    "Homestay not found with id",
                    ENTITY_NAME,
                    "homestaynotfound");
        }

        // Kiểm tra folder
        if (!FOLDER_PATTERN.matcher(folder).matches()) {
            throw new BadRequestAlertException(
                    "Invalid folder name: " + folder + ". Only letters, numbers, underscores, and hyphens are allowed",
                    ENTITY_NAME,
                    "invalidfolder");
        }
    }

    void validateFile(MultipartFile[] files) {
        // Kiểm tra số lượng file
        if (files.length > MAX_FILES) {
            throw new BadRequestAlertException(
                    "Cannot upload more than " + MAX_FILES + " files",
                    ENTITY_NAME,
                    "toomanyfiles");
        }

        // Kiểm tra từng file
        for (MultipartFile file : files) {
            // Kiểm tra file rỗng
            if (file.isEmpty()) {
                throw new BadRequestAlertException(
                        "File " + file.getOriginalFilename() + " is empty",
                        ENTITY_NAME,
                        "emptyfile");
            }

            // Kiểm tra kích thước file
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new BadRequestAlertException(
                        "File " + file.getOriginalFilename() + " exceeds maximum size of 5MB",
                        ENTITY_NAME,
                        "filetoolarge");
            }

            // Kiểm tra định dạng file
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !isAllowedExtension(originalFilename)) {
                throw new BadRequestAlertException(
                        "File " + originalFilename + " has an invalid extension. Allowed: " + ALLOWED_EXTENSIONS,
                        ENTITY_NAME,
                        "invalidfileextension");
            }
        }
    }

    private boolean isAllowedExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @Override
    public List<HomestayImage> findHomestayImageByHomestayId(long id) {
        log.debug("find HomestayImage by homestayId: {}", id);
        if (!homestayRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Homestay with ID: " + id + " does not exist",
                    ENTITY_NAME,
                    "homestaynotfound");
        }
        return this.homestayImageRepository.findByHomestayId(id);
    }

    @Override
    @Transactional
    public void deleteImage(long id) {
        log.debug("delete HomestayImage by id: {}", id);
        // Find the image in the database
        HomestayImage homestayImage = homestayImageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Image with ID: " + id + " not found", ENTITY_NAME, "imagenotfound"));

        // Delete from Cloudinary
        try {
            cloudinary.uploader().destroy(homestayImage.getPublicId(), ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new BadRequestAlertException(
                    "Failed to delete image with public ID: " + homestayImage.getId(), ENTITY_NAME, "deletefailed");
        }

        // Check if the image exists in homestay and remove
        homestayImage.getHomestay().getImages().remove(homestayImage);

        // Delete from database
        homestayImageRepository.delete(homestayImage);
    }

    @Override
    public void deleteByImageUrl(String url) {
        log.debug("delete HomestayImage by url: {}", url);
        // Find the image in the database
        HomestayImage homestayImage = homestayImageRepository.findByImageUrl(url);

        // Delete from Cloudinary
        try {
            cloudinary.uploader().destroy(homestayImage.getPublicId(), ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new BadRequestAlertException(
                    "Failed to delete image with public ID: " + homestayImage.getId(), ENTITY_NAME, "deletefailed");
        }

        // Check if the image exists in homestay and remove
        homestayImage.getHomestay().getImages().remove(homestayImage);

        // Delete from database
        homestayImageRepository.delete(homestayImage);
    }

}
