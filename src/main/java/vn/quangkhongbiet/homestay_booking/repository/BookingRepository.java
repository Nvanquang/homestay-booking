package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    boolean existsById(Long id);

    List<Booking> findByIdIn(List<Long> ids);

    List<Booking> findByUser(User user);

    boolean existsByUserIdAndHomestayIdAndStatusIn(Long userId, Long homestayId, List<BookingStatus> statuses);

    Optional<Booking> findByIdAndUserIdAndStatusIn(
            Long id, Long userId, List<BookingStatus> statuses);

    @Modifying
    @Query("""
            UPDATE Booking b
            SET b.status = :newStatus
            WHERE b.status = :oldStatus AND b.checkoutDate = CURRENT_DATE
            """)
    void updateStatusBooking(
        @Param("newStatus") BookingStatus newStatus,
        @Param("oldStatus") BookingStatus oldStatus);
}
