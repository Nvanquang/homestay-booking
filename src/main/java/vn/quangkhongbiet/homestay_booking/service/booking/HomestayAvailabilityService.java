package vn.quangkhongbiet.homestay_booking.service.booking;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;
import vn.quangkhongbiet.homestay_booking.repository.HomestayAvailabilityRepository;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Service
@RequiredArgsConstructor
public class HomestayAvailabilityService {

    private static final Integer NIGHT_MAX = 365;
    
    private static final Logger log = LoggerFactory.getLogger(HomestayAvailabilityService.class);

    private final HomestayAvailabilityRepository availabilityRepository;

    public List<HomestayAvailability> checkAvailabilityForBooking(final Long homestayId,
                                                                  final LocalDate checkinDate,
                                                                  final LocalDate checkoutDate) {
        log.debug("check HomestayAvailability for booking, homestayId: {}, checkinDate: {}, checkoutDate: {}", homestayId, checkinDate, checkoutDate);

        int nights = (int) Duration.between(checkinDate.atStartOfDay(), checkoutDate.atStartOfDay()).toDays();
        if (nights > NIGHT_MAX) {
            throw new BadRequestAlertException("Number of nights cannot exceed 365 days!", "homestayavailability", "nightsvalid");
        }

        final var aDays = availabilityRepository.findByHomestayIdAndStatusAndDateBetween(
                homestayId,
                AvailabilityStatus.AVAILABLE,
                checkinDate,
                checkoutDate.minusDays(1)
        );
        if (aDays.isEmpty() || aDays.size() < nights) {
            throw new BadRequestAlertException("Homestay has been rented!", "homestayAvailability", "homestaybusy");
        }

        return aDays;
    }


    @Transactional
    public void saveAll(List<HomestayAvailability> aDays) {
        log.debug("save all HomestayAvailability: {}", aDays != null ? aDays.size() : 0);
        availabilityRepository.saveAll(aDays);
    }

    public HomestayAvailability findById(HomestayAvailabilityId id) {
        log.debug("find HomestayAvailability by id: {}", id);
        return this.availabilityRepository.findById(id).orElseThrow(() -> 
            new EntityNotFoundException (ErrorConstants.ENTITY_NOT_FOUND_TYPE, "HomestayAvailability not found with id", "HomestayAvailability", "homestayavailabilitynotfound"));
    }

}
