package vn.quangkhongbiet.homestay_booking.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;

@Repository
public interface HomestayAvailabilityRepository extends JpaRepository<HomestayAvailability, HomestayAvailabilityId>, JpaSpecificationExecutor<HomestayAvailability> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<HomestayAvailability> findByHomestayIdAndStatusAndDateBetween(Long homestayId, AvailabilityStatus status, LocalDate checkinDate, LocalDate checkoutDate);

    List<HomestayAvailability> findByDate(Instant date);

    List<HomestayAvailability> findByHomestayIdAndDateBetween(Long homestayId, LocalDate checkinDate, LocalDate checkoutDate);
}
