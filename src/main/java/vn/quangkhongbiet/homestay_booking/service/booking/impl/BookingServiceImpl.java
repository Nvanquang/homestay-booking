package vn.quangkhongbiet.homestay_booking.service.booking.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.BookingPrice;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResHomestay;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResUser;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.repository.BookingRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.service.booking.PriceService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final String ENTITY_NAME = "booking";
    private final BookingRepository bookingRepository;
    private final HomestayRepository homestayRepository;
    private final HomestayAvailabilityService availabilityService;
    private final PriceService priceService;
    private final UserRepository userRepository;

    @Override
    public Boolean existsById(Long id) {
        return this.bookingRepository.existsById(id);
    }

    @Override
    @SneakyThrows
    @Transactional
    public Booking createBooking(ReqBooking request) {
        validateRequest(request);
        validateHomestay(request);

        final Long homestayId = request.getHomestayId();
        final LocalDate checkinDate = request.getCheckinDate();
        final LocalDate checkoutDate = request.getCheckoutDate();

        final var aDays = availabilityService.checkAvailabilityForBooking(homestayId, checkinDate, checkoutDate);

        Thread.sleep(5000);

        final BookingPrice price = priceService.calculate(aDays);
        final Booking booking = Booking.builder()
                .id(homestayId)
                .user(this.userRepository.findById(request.getUserId()).get())
                .homestay(this.homestayRepository.findById(request.getHomestayId()).get())
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .guests(request.getGuests())
                .subtotal(price.getSubtotal())
                .discount(price.getDiscount())
                .totalAmount(price.getTotalAmount())
                .note(request.getNote())
                .status(BookingStatus.BOOKED) // đă đặt
                .paymentStatus(PaymentStatus.PENDING) // chưa thanh toán
                .build();

        aDays.forEach(a -> a.setStatus(AvailabilityStatus.BOOKED));

        availabilityService.saveAll(aDays);
        bookingRepository.save(booking);
        return booking;
    }

    public void validateRequest(ReqBooking request) {
        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = request.getCheckoutDate();
        LocalDate currentDate = LocalDate.now();
        // check user, homestay
        if (!this.userRepository.existsById(request.getUserId())) {
            throw new EntityNotFoundException("User not foud with id!", ENTITY_NAME, "usernotfound");
        }

        if (!this.homestayRepository.existsById(request.getUserId())) {
            throw new EntityNotFoundException("Homestay not foud with id!", ENTITY_NAME, "usernotfound");
        }

        if (checkinDate.isBefore(currentDate) || checkinDate.isAfter(checkoutDate)) {
            throw new BadRequestAlertException(ErrorConstants.CHECKIN_DATE_INVALID, "Invalid checkin date!",
                    ENTITY_NAME, "checkininvalid");
        }
    }

    public void validateHomestay(ReqBooking request) {
        Homestay homestay = homestayRepository.findById(request.getHomestayId()).orElseThrow(() -> new EntityNotFoundException(
                ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Homestay not found with id",
                ENTITY_NAME, "homestaynotfound"));

        if (homestay.getStatus() != HomestayStatus.ACTIVE) {
            throw new BadRequestAlertException(ErrorConstants.HOMESTAY_NOT_ACTIVE, "Homestay is not operating!",
                    ENTITY_NAME, "homestaynotactive");
        }

        if (homestay.getGuests() < request.getGuests()) {
            throw new BadRequestAlertException(ErrorConstants.GUESTS_INVALID, "Invalid number of guests!",
                    ENTITY_NAME, "guestsinvalid");
        }
    }

    @Override
    public Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException (
                ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Booking not found with id", ENTITY_NAME,
                "bookingnotfound"));
    }

    @Override
    public PagedResponse findAllBookings(Specification<Booking> spec, Pageable pageable) {
        Page<Booking> pageBookings = this.bookingRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageBookings.getTotalPages());
        meta.setTotal(pageBookings.getTotalElements());

        result.setMeta(meta);

        List<ResBookingDTO> bookings = pageBookings.getContent().stream().map(item -> this.convertToResBookingDTO(item))
                .toList();
        result.setResult(bookings);
        return result;
    }

    @Override
    public Booking updatePartialBooking(UpdateBookingDTO dto) {
        return bookingRepository.findById(dto.getId()).map(existingBooking -> {
            validateBookingStatus(existingBooking);
            validateNewStatus(existingBooking, dto);
            validatePaymentStatus(existingBooking, dto);
            updateBookingFields(existingBooking, dto);
            return bookingRepository.save(existingBooking);
        }).orElseThrow(() -> new EntityNotFoundException(ErrorConstants.ENTITY_NOT_FOUND_TYPE,
                "Booking not found with id" + dto.getId(), ENTITY_NAME, "bookingnotfound"));
    }

    private void validateBookingStatus(Booking existingBooking) {
        if (existingBooking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestAlertException("Unable to update cancelled bookings", ENTITY_NAME, "bookingcancelled");
        }
        if (existingBooking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestAlertException("Unable tp update completed bookings", ENTITY_NAME,
                    "bookingcompleted");
        }
    }

    private void validateNewStatus(Booking existingBooking, UpdateBookingDTO dto) {
        if (dto.getStatus() == null)
            return;
        if (existingBooking.getStatus() == BookingStatus.BOOKED
                && (dto.getStatus() != BookingStatus.COMPLETED
                        && dto.getStatus() != BookingStatus.CANCELLED)) {
            throw new BadRequestAlertException(
                    "Status can only change from BOOKED to COMPLETED or CANCELLED", ENTITY_NAME,
                    "bookingstatusinvalid");
        }
    }

    private void validatePaymentStatus(Booking existingBooking, UpdateBookingDTO dto) {
        if (dto.getPaymentStatus() == null)
            return;
        if (existingBooking.getPaymentStatus() == PaymentStatus.COMPLETED
                && dto.getPaymentStatus() == PaymentStatus.FAILED) {
            throw new BadRequestAlertException("Cannot change from COMPLETED to FAILED", ENTITY_NAME,
                    "paymentstatusinvalid");
        }
    }

    private void updateBookingFields(Booking existingBooking, UpdateBookingDTO dto) {
        if (dto.getStatus() != null) {
            existingBooking.setStatus(dto.getStatus());
        }
        if (dto.getPaymentStatus() != null) {
            existingBooking.setPaymentStatus(dto.getPaymentStatus());
        }
        if (dto.getPaymentDate() != null && dto.getStatus() == BookingStatus.COMPLETED) {
            existingBooking.setPaymentDate(dto.getPaymentDate());
        }
    }

    @Override
    public ResBookingDTO convertToResBookingDTO(Booking booking) {
        var builder = ResBookingDTO.builder()
                .id(booking.getId())
                .checkinDate(booking.getCheckinDate())
                .checkoutDate(booking.getCheckoutDate())
                .guests(booking.getGuests())
                .status(booking.getStatus())
                .subtotal(booking.getSubtotal())
                .discount(booking.getDiscount())
                .totalAmount(booking.getTotalAmount())
                .note(booking.getNote())
                .paymentStatus(booking.getPaymentStatus())
                .paymentMethod(booking.getPaymentMethod())
                .paymentDate(booking.getPaymentDate());

        // convert User to ResUser
        ResUser resUser = booking.getUser() != null
                ? new ResUser(booking.getUser().getId(), booking.getUser().getFullName())
                : new ResUser(null, null);
        builder.user(resUser);

        // convert Homestay to ResHomestay
        ResHomestay resHomestay = booking.getHomestay() != null ? new ResHomestay(booking.getHomestay().getId(),
                booking.getHomestay().getName(), booking.getHomestay().getAddress())
                : new ResHomestay(null, null, null);
        builder.homestay(resHomestay);
        return builder.build();
    }

}