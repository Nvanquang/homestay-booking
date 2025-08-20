package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ReviewTotalResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    boolean existsByBookingId(Long bookingId);

    List<Review> findByHomestayId(Long homestayId);

    @Query(value = """
            SELECT 
                COUNT(*) AS totalReviews,
                COALESCE(AVG(rating), 0) AS averageRating
            FROM reviews
            WHERE homestay_id = :homestayId;
            """, nativeQuery = true)
    ReviewTotalResponse findTotalReviewsByHomestayId(@Param("homestayId") Long homestayId);
}
