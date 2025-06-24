package vn.quangkhongbiet.homestay_booking.service.homestay;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface ReviewService {

    ResReviewDTO createReview(ReqReviewDTO dto);

    PagedResponse getReviewsForHomestay(Specification<Review> spec, Pageable pageable);

    ResReviewDTO convertToResReviewDTO(Review review);
}