package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    boolean existsById(long id);

    List<Booking> findByIdIn(List<Long> ids);
}
