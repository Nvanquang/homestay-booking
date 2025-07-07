package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqHomestaySearch;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResSearchHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing homestay operations such as creation, update, search, and deletion.
 */
public interface HomestayService {
    /**
     * Checks if a homestay exists by its ID.
     *
     * @param id the ID of the homestay
     * @return true if the homestay exists, false otherwise
     * @throws EntityNotFoundException if the homestay is not found
     */
    Boolean existsById(Long id);

    /**
     * Creates a new homestay with associated images.
     *
     * @param homestay the Homestay entity to create
     * @param files    the array of image files to upload
     * @param folder   the folder to store images in
     * @return the created homestay as a response DTO
     * @throws BadRequestAlertException if input data is invalid or upload fails
     * @throws EntityNotFoundException if related entity is not found
     */
    ResHomestayCreateDTO createHomestay(Homestay homestay, MultipartFile[] files, String folder);

    /**
     * Adds amenities to a specific homestay.
     *
     * @param homestayId the ID of the homestay
     * @param amenityIds the list of amenity IDs to add
     * @return the updated homestay as a response DTO
     * @throws BadRequestAlertException if input data is invalid
     * @throws EntityNotFoundException if the homestay or amenities are not found
     */
    ResHomestayUpdatedDTO addAmenitiesToHomestay(long homestayId, List<Long> amenityIds);

    /**
     * Finds a homestay by its ID.
     *
     * @param id the ID of the homestay
     * @return the Homestay entity
     * @throws EntityNotFoundException if the homestay is not found
     */
    Homestay findHomestayById(Long id);

    /**
     * Searches for homestays based on the given search request.
     *
     * @param request the search request DTO
     * @return a list of search result DTOs
     * @throws BadRequestAlertException if search parameters are invalid
     */
    List<ResSearchHomestayDTO> searchHomestays(ReqHomestaySearch request);

    /**
     * Finds all homestays matching the given specification and pageable.
     *
     * @param spec     the specification for filtering
     * @param pageable the pagination information
     * @return a paged response of homestays
     * @throws BadRequestAlertException if query parameters are invalid
     */
    PagedResponse findAllHomestays(Specification<Homestay> spec, Pageable pageable);

    /**
     * Partially updates a homestay with the given data.
     *
     * @param homestay the DTO containing update information
     * @return the updated homestay as a response DTO
     * @throws BadRequestAlertException if update data is invalid
     * @throws EntityNotFoundException if the homestay is not found
     */
    ResHomestayUpdatedDTO updatePartialHomestay(UpdateHomestayDTO homestay);

    /**
     * Deletes a homestay by its ID.
     *
     * @param id the ID of the homestay to delete
     * @throws BadRequestAlertException if the homestay cannot be deleted
     * @throws EntityNotFoundException if the homestay is not found
     */
    void deleteHomestay(Long id);

    /**
     * Converts a Homestay entity to a response DTO for creation.
     *
     * @param homestay the Homestay entity
     * @return the response DTO for created homestay
     */
    ResHomestayCreateDTO convertToResCreateHomestayDTO(Homestay homestay);

    /**
     * Converts a Homestay entity to a response DTO for update.
     *
     * @param homestay the Homestay entity
     * @return the response DTO for updated homestay
     */
    ResHomestayUpdatedDTO convertToResUpdatedHomestayDTO(Homestay homestay);

    /**
     * Converts a Homestay entity to a response DTO.
     *
     * @param homestay the Homestay entity
     * @return the response DTO for the homestay
     */
    ResHomestayDTO convertToResHomestayDTO(Homestay homestay);
}
