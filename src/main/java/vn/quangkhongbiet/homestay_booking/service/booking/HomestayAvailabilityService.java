package vn.quangkhongbiet.homestay_booking.service.booking;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.CreateAvailabilityRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailabilityId;
import vn.quangkhongbiet.homestay_booking.repository.HomestayAvailabilityRepository;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomestayAvailabilityService {

    private static final Integer NIGHT_MAX = 365;

    private final HomestayAvailabilityRepository availabilityRepository;

    public List<HomestayAvailability> checkAvailabilityForBooking(final Long homestayId,
            final LocalDate checkinDate,
            final LocalDate checkoutDate) {
        log.debug("check HomestayAvailability for booking, homestayId: {}, checkinDate: {}, checkoutDate: {}",
                homestayId, checkinDate, checkoutDate);

        int nights = (int) Duration.between(checkinDate.atStartOfDay(), checkoutDate.atStartOfDay()).toDays();
        if (nights > NIGHT_MAX) {
            throw new BadRequestAlertException("Number of nights cannot exceed 365 days!", "homestayavailability",
                    "nightsvalid");
        }

        final var aDays = availabilityRepository.findByHomestayIdAndStatusAndDateBetween(
                homestayId,
                AvailabilityStatus.AVAILABLE,
                checkinDate,
                checkoutDate.minusDays(1));
        if (aDays.isEmpty() || aDays.size() < nights) {
            throw new ConflictException("Homestay has been rented!", "homestayAvailability", "homestaybusy");
        }

        return aDays;
    }

    @Transactional
    public List<HomestayAvailability> saveAll(CreateAvailabilityRequest aDays) {
        log.debug("save all HomestayAvailability: {}", aDays != null ? aDays.getDates().size() : 0);
        List<HomestayAvailability> availabilities = aDays.getDates().stream()
                .map(date -> {
                    HomestayAvailability availability = new HomestayAvailability();
                    availability.setHomestayId(aDays.getHomestayId());
                    availability.setDate(date);
                    availability.setPrice(aDays.getPrice());
                    availability.setStatus(aDays.getStatus());
                    return availability;
                })
                .collect(Collectors.toList());
        return this.availabilityRepository.saveAll(availabilities);
    }

    @Transactional
    public void saveAll(List<HomestayAvailability> aDays) {
        log.debug("save all HomestayAvailability: {}", aDays != null ? aDays.size() : 0);
        this.availabilityRepository.saveAll(aDays);
    }

    public HomestayAvailability updateHomestayAvailability(HomestayAvailability request) {
        log.debug("update HomestayAvailability: {}", request);
        HomestayAvailabilityId id = new HomestayAvailabilityId(request.getHomestayId(), request.getDate());

        HomestayAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found", "HomestayAvailability", "homestayavailabilitynotfound"));

        availability.setPrice(request.getPrice());
        availability.setStatus(request.getStatus());

        return availabilityRepository.save(availability);
    }

    public HomestayAvailability findById(HomestayAvailabilityId id) {
        log.debug("find HomestayAvailability by id: {}", id);
        return this.availabilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorConstants.ENTITY_NOT_FOUND_TYPE,
                        "HomestayAvailability not found with id", "HomestayAvailability",
                        "homestayavailabilitynotfound"));
    }

    public List<HomestayAvailability> getRange(final Long homestayId,
            final LocalDate checkinDate,
            final LocalDate checkoutDate) {
        return availabilityRepository.findByHomestayIdAndDateBetween(homestayId, checkinDate,
                checkoutDate.minusDays(1));
    }

    public PagedResponse findAllAvailabilities(Specification<HomestayAvailability> spec, Pageable pageable) {
        log.debug("find all Booking with spec: {}, pageable: {}", spec, pageable);
        Page<HomestayAvailability> pageBookings = this.availabilityRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBookings.getTotalPages());
        meta.setTotal(pageBookings.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageBookings.getContent());
        return result;
    }

}
