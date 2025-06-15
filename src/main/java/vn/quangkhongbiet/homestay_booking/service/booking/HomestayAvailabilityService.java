package vn.quangkhongbiet.homestay_booking.service.booking;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;
import vn.quangkhongbiet.homestay_booking.repository.HomestayAvailabilityRepository;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Service
@RequiredArgsConstructor
public class HomestayAvailabilityService {
    private static final Integer NIGHT_MAX = 365;

    private final HomestayAvailabilityRepository availabilityRepository;

    public List<HomestayAvailability> checkAvailabilityForBooking(final Long homestayId,
                                                                  final LocalDate checkinDate,
                                                                  final LocalDate checkoutDate) {

        int nights = (int) Duration.between(checkinDate.atStartOfDay(), checkoutDate.atStartOfDay()).toDays();
        if (nights > NIGHT_MAX) {
            throw new BadRequestAlertException(ErrorConstants.NIGHTS_INVALID, "Number of nights cannot exceed 365 days!", "homestayavailability", "nightsvalid");
        }

        final var aDays = availabilityRepository.findByHomestayIdAndStatusAndDateBetween(
                homestayId,
                AvailabilityStatus.AVAILABLE,
                checkinDate,
                checkoutDate.minusDays(1)
        );
        if (aDays.isEmpty() || aDays.size() < nights) {
            throw new BusinessException(ErrorConstants.HOMESTAY_BUSY, "Homestay has been rented!", "homestayAvailability", "homestaybusy");
        }

        return aDays;
    }


    @Transactional
    public void saveAll(List<HomestayAvailability> aDays) {
        availabilityRepository.saveAll(aDays);
    }

    public HomestayAvailability findById(HomestayAvailabilityId id) {
        return this.availabilityRepository.findById(id).orElseThrow(() -> 
            new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "HomestayAvailability not found with id", "HomestayAvailability", "homestayavailabilitynotfound"));
    }

}
