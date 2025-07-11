package vn.quangkhongbiet.homestay_booking.service.homestay;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateAmenityRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException;

/**
 * Service interface for managing amenities.
 * Provides methods for creating, querying, and deleting amenities.
 */
public interface AmenityService {
    /**
     * Create a new amenity.
     * @param amenity the amenity entity to create.
     * @return the created amenity entity.
     * @throws ConflictException if amenity already exists.
     */
    Amenity createAmenity(CreateAmenityRequest amenity);

    /**
     * Find an amenity by id.
     * @param id the amenity id.
     * @return the amenity entity.
     * @throws EntityNotFoundException if amenity not found.
     */
    Amenity findAmenityById(Long id);

    /**
     * Get all amenities with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of amenities.
     */
    PagedResponse findAllAmenities(Specification<Amenity> spec, Pageable pageable);

    /**
     * Delete an amenity by id.
     * @param id the amenity id.
     * @throws EntityNotFoundException if amenity not found.
     */
    void deleteAmenity(Long id);
}
