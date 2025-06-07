package vn.quangkhongbiet.homestay_booking.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;

@Repository
public interface HomestayAvailabilityRepository extends JpaRepository<HomestayAvailability, HomestayAvailabilityId> {
    
    List<HomestayAvailability> findByHomestayIdAndStatusAndDateBetween(Long homestayId, AvailabilityStatus status, LocalDate checkinDate, LocalDate checkoutDate);

    List<HomestayAvailability> findByDate(Instant date);
}
