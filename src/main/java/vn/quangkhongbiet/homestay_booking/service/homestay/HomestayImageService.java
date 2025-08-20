package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

/**
 * Service interface for managing images related to a homestay.
 */
public interface HomestayImageService {
    /**
     * Creates and saves images for a specific homestay.
     *
     * @param files      the array of image files to upload
     * @param homestayId the ID of the homestay
     * @param folder     the folder to store images in
     * @return a list of created HomestayImage entities
     * @throws BadRequestAlertException if input data is invalid or upload fails
     * @throws EntityNotFoundException if the homestay is not found
     */
    void createHomestayImages(MultipartFile[] files, Long homestayId, String folder);

    /**
     * Finds all images associated with a specific homestay.
     *
     * @param id the ID of the homestay
     * @return a list of HomestayImage entities
     * @throws EntityNotFoundException if the homestay is not found
     */
    List<HomestayImage> findHomestayImageByHomestayId(long id);

    /**
     * Deletes an image by its ID.
     *
     * @param id the ID of the image to delete
     * @throws BadRequestAlertException if the image cannot be deleted or input is invalid
     * @throws EntityNotFoundException if the image is not found
     */
    void deleteImage(long id);

    void deleteByImageUrl(String url);

}
