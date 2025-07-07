package vn.quangkhongbiet.homestay_booking.service.homestay;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing reviews.
 * Provides methods for creating, querying, and converting reviews.
 */
public interface ReviewService {
    /**
     * Create a new review.
     * @param dto the review request DTO.
     * @return the created review DTO.
     * @throws EntityNotFoundException if user or homestay not found.
     * @throws BadRequestAlertException if user chưa từng đặt homestay.
     * @throws ConflictException nếu đã đánh giá rồi.
     */
    ResReviewDTO createReview(ReqReviewDTO dto);

    /**
     * find reviews for a homestay with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of reviews.
     */
    PagedResponse findReviewsForHomestay(Specification<Review> spec, Pageable pageable);

    /**
     * Convert review entity to review DTO.
     * @param review the review entity.
     * @return the review DTO.
     */
    ResReviewDTO convertToResReviewDTO(Review review);
}