package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateReviewRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ReviewResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.BookingRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.ReviewRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.ReviewService;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final String ENTITY_NAME = "review";

    private final ReviewRepository reviewRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final HomestayRepository homestayRepository;

    @Override
    public ReviewResponse createReview(CreateReviewRequest dto) {
        log.debug("create Review with request: {}", dto);
        String email = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        if (!validateUserCanReviewHomestay(dto.getBookingId(), user.getId(), dto.getHomestayId())) {
            throw new BadRequestAlertException("You cannot review this homestay", ENTITY_NAME, "cannotreview");
        }

        List<BookingStatus> validStatuses = List.of(BookingStatus.BOOKED, BookingStatus.COMPLETED);
        Optional<Booking> bookingOpt = bookingRepository
                .findByIdAndUserIdAndStatusIn(dto.getBookingId(), user.getId(), validStatuses);

        if (bookingOpt.isEmpty()) {
            throw new BadRequestAlertException("You have never booked this homestay, cannot rate", ENTITY_NAME, "cannotrate");
        }
        Booking booking = bookingOpt.get();

        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .postingDate(Instant.now())
                .user(userRepository.getReferenceById(user.getId()))
                .homestay(homestayRepository.getReferenceById(dto.getHomestayId()))
                .booking(booking)
                .build();
    
        ReviewResponse resReviewDTO = this.convertToResReviewDTO(reviewRepository.save(review));
        return resReviewDTO;
    }

    private Boolean validateUserCanReviewHomestay(Long bookingId, Long userId, Long homestayId) {
        if (!homestayRepository.existsById(homestayId)) {
            return false;
        }

        List<BookingStatus> validStatuses = List.of(BookingStatus.BOOKED, BookingStatus.COMPLETED);
        Optional<Booking> bookingOpt = bookingRepository
                .findByIdAndUserIdAndStatusIn(bookingId, userId, validStatuses);

        if (bookingOpt.isEmpty()) {
            return false;
        }

        Booking booking = bookingOpt.get();

        if (reviewRepository.existsByBookingId(booking.getId())) {
            return false;
        }

        return true; // User can review the homestay
    }

    @Override
    public PagedResponse findReviewsForHomestay(Specification<Review> spec, Pageable pageable) {
        log.debug("find all Review with spec: {}, pageable: {}", spec, pageable);
        Page<Review> pageReviews = this.reviewRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageReviews.getTotalPages());
        meta.setTotal(pageReviews.getTotalElements());

        result.setMeta(meta);
        List<ReviewResponse> reviews = pageReviews.getContent().stream().map(item -> this.convertToResReviewDTO(item))
                .toList();
        result.setResult(reviews);
        return result;
    }

    @Override
    public ReviewResponse convertToResReviewDTO(Review review) {
        ReviewResponse.ReviewerInfo user = new ReviewResponse.ReviewerInfo(
                review.getUser().getId(),
                review.getUser().getFullName(),
                review.getUser().getAvatar());

        return ReviewResponse.builder()
                .rating(review.getRating())
                .comment(review.getComment())
                .postingDate(review.getPostingDate())
                .user(user)
                .build();
    }

    @Override
    public Boolean checkReviewed(Long bookingId, Long userId, Long homestayId) {
        if (this.validateUserCanReviewHomestay(bookingId, userId, homestayId)) {
            return false;
        } else {
            return true;
        }
    }
}
