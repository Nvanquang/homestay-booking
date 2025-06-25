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
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.BookingPrice;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingStatusDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResVnpBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.request.InitPaymentRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.InitPaymentResponse;
import vn.quangkhongbiet.homestay_booking.repository.BookingRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.service.booking.HomestayAvailabilityService;
import vn.quangkhongbiet.homestay_booking.service.booking.PriceService;
import vn.quangkhongbiet.homestay_booking.service.payment.VnpayPaymentService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final String ENTITY_NAME = "booking";

    private final BookingRepository bookingRepository;

    private final HomestayRepository homestayRepository;

    private final HomestayAvailabilityService availabilityService;

    private final PriceService priceService;

    private final UserRepository userRepository;

    private final VnpayPaymentService vnpayPaymentService;

    @Override
    public Boolean existsById(Long id) {
        log.debug("check Booking exists by id: {}", id);
        return this.bookingRepository.existsById(id);
    }

    @Override
    @SneakyThrows
    @Transactional
    public ResVnpBookingDTO createBooking(ReqBooking request) {
        validateRequest(request);
        validateHomestay(request);

        final Long homestayId = request.getHomestayId();
        final LocalDate checkinDate = request.getCheckinDate();
        final LocalDate checkoutDate = request.getCheckoutDate();

        log.debug("[request_id={}] User user_id={} is acquiring lock homestay_id={} from checkin_date={} to checkout_date={}", request.getRequestId(), request.getUserId(), homestayId, checkinDate, checkoutDate);
        final var aDays = availabilityService.checkAvailabilityForBooking(homestayId, checkinDate, checkoutDate);
        log.debug("[request_id={}] User user_id={} locked homestay_id={} from checkin_date={} to checkout_date={}", request.getRequestId(), request.getUserId(), request.getHomestayId(), checkinDate, checkoutDate);

        Thread.sleep(5000);

        final BookingPrice price = priceService.calculate(aDays);
        final Booking booking = Booking.builder()
                .user(this.userRepository.findById(request.getUserId()).get())
                .homestay(this.homestayRepository.findById(request.getHomestayId()).get())
                .checkinDate(checkinDate)
                .checkoutDate(checkoutDate)
                .guests(request.getGuests())
                .subtotal(price.getSubtotal())
                .discount(price.getDiscount())
                .totalAmount(price.getTotalAmount())
                .note(request.getNote())
                .status(BookingStatus.PAYMENT_PROCESSING) // đang trong tien trinh thanh toan
                .requestId(request.getRequestId())
                .build();

        aDays.forEach(a -> a.setStatus(AvailabilityStatus.HELD));

        availabilityService.saveAll(aDays);
        Booking bookingDB = bookingRepository.save(booking);

        InitPaymentRequest initPaymentRequest = InitPaymentRequest.builder()
                .userId(booking.getUser().getId())
                .amount(booking.getTotalAmount().longValue())
                .txnRef(String.valueOf(bookingDB.getId()))
                .requestId(booking.getRequestId())
                .ipAddress(request.getIpAddress())
                .build();

        InitPaymentResponse initPaymentResponse = vnpayPaymentService.init(initPaymentRequest);

        log.info("[request_id={}] User user_id={} created booking_id={} successfully", request.getRequestId(), request.getUserId(), booking.getId());
        return ResVnpBookingDTO.builder()
                .booking(this.convertToResBookingDTO(booking))
                .payment(initPaymentResponse)
                .build();
    }

    @Override
    public Booking markBooked(Long bookingId){
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found", ENTITY_NAME, "bookingnotfound"));

        List<HomestayAvailability> aDays = this.availabilityService.getRange(booking.getHomestay().getId(), booking.getCheckinDate(), booking.getCheckoutDate());
        booking.setStatus(BookingStatus.BOOKED);
        aDays.forEach(day -> day.setStatus(AvailabilityStatus.BOOKED));

        availabilityService.saveAll(aDays);
        return bookingRepository.save(booking);
    }

    @Override
    public ResBookingStatusDTO getBookingStatus(Long id) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found", ENTITY_NAME, "bookingnotfound"));
        return ResBookingStatusDTO.builder()
        .bookingId(booking.getId())
        .userId(booking.getUser().getId())
        .homestayId(booking.getHomestay().getId())
        .status(booking.getStatus())
        .build();
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
            throw new BadRequestAlertException("Invalid checkin date!",
                    ENTITY_NAME, "checkininvalid");
        }
    }

    public void validateHomestay(ReqBooking request) {
        Homestay homestay = homestayRepository.findById(request.getHomestayId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Homestay not found with id",
                        ENTITY_NAME, "homestaynotfound"));

        if (homestay.getStatus() != HomestayStatus.ACTIVE) {
            throw new BadRequestAlertException("Homestay is not operating!",
                    ENTITY_NAME, "homestaynotactive");
        }

        if (homestay.getGuests() < request.getGuests()) {
            throw new BadRequestAlertException("Invalid number of guests!",
                    ENTITY_NAME, "guestsinvalid");
        }
    }

    @Override
    public Booking findBookingById(Long id) {
        log.debug("find Booking by id: {}", id);
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Booking not found with id", ENTITY_NAME,
                "bookingnotfound"));
    }

    @Override
    public List<ResBookingDTO> findBookingByUser(Long userId) {
        List<Booking> bookingHistory = this.bookingRepository.findByUser(this.userRepository.findById(userId).get());
        return bookingHistory.stream().map(item -> this.convertToResBookingDTO(item)).toList();
    }

    @Override
    public PagedResponse findAllBookings(Specification<Booking> spec, Pageable pageable) {
        log.debug("find all Booking with spec: {}, pageable: {}", spec, pageable);
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
        log.debug("update Booking partially with dto: {}", dto);
        return bookingRepository.findById(dto.getId()).map(existingBooking -> {
            validateBookingStatus(existingBooking);
            validateNewStatus(existingBooking, dto);
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

    private void updateBookingFields(Booking existingBooking, UpdateBookingDTO dto) {
        if (dto.getStatus() != null) {
            existingBooking.setStatus(dto.getStatus());
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
                .note(booking.getNote());

        // convert User to ResUser
        ResBookingDTO.ResUser resUser = booking.getUser() != null
                ? new ResBookingDTO.ResUser(booking.getUser().getId(), booking.getUser().getFullName())
                : new ResBookingDTO.ResUser(null, null);
        builder.user(resUser);

        // convert Homestay to ResHomestay
        ResBookingDTO.ResHomestay resHomestay = booking.getHomestay() != null
                ? new ResBookingDTO.ResHomestay(booking.getHomestay().getId(),
                        booking.getHomestay().getName(), booking.getHomestay().getAddress())
                : new ResBookingDTO.ResHomestay(null, null, null);
        builder.homestay(resHomestay);
        return builder.build();
    }

}