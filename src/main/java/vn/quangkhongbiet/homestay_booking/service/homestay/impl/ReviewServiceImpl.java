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
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResReviewDTO;
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
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException;
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
    public ResReviewDTO createReview(ReqReviewDTO dto) {
        log.debug("create Review with request: {}", dto);
        String email = SecurityUtil.getCurrentUserLogin() != null ? SecurityUtil.getCurrentUserLogin().get() : "";
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));

        // validate information
        Booking booking = this.validateUserCanReviewHomestay(user.getId(), dto.getHomestayId());

        // Tạo review
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setPostingDate(Instant.now());
        review.setUser(userRepository.getReferenceById(user.getId()));
        review.setHomestay(homestayRepository.getReferenceById(dto.getHomestayId()));
        review.setBooking(booking);

        ResReviewDTO resReviewDTO = this.convertToResReviewDTO(reviewRepository.save(review));
        return resReviewDTO;
    }

    private Booking validateUserCanReviewHomestay(Long userId, Long homestayId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound");
        }

        if (!homestayRepository.existsById(homestayId)) {
            throw new EntityNotFoundException("Homestay not found", ENTITY_NAME, "homestaynotfound");
        }

        List<BookingStatus> validStatuses = List.of(BookingStatus.BOOKED, BookingStatus.COMPLETED);
        Optional<Booking> bookingOpt = bookingRepository
                .findTopByUserIdAndHomestayIdAndStatusInOrderByCheckoutDateDesc(userId, homestayId, validStatuses);

        if (bookingOpt.isEmpty()) {
            throw new BadRequestAlertException("You have never booked this homestay, cannot rate", ENTITY_NAME,
                    "cannotrate");
        }

        Booking booking = bookingOpt.get();

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ConflictException("You have already rated this homestay", ENTITY_NAME, "haverated");
        }

        return booking;
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
        List<ResReviewDTO> reviews = pageReviews.getContent().stream().map(item -> this.convertToResReviewDTO(item)).toList();
        result.setResult(reviews);
        return result;
    }

    @Override
    public ResReviewDTO convertToResReviewDTO(Review review) {
        ResReviewDTO.ReviewerInfo user = new ResReviewDTO.ReviewerInfo(
            review.getUser().getId(),
            review.getUser().getFullName(),
            null
        );

        return ResReviewDTO.builder()
            .rating(review.getRating())
            .comment(review.getComment())
            .postingDate(review.getPostingDate())
            .hostReply(review.getHostReply())
            .user(user)
            .build();
    }
}
