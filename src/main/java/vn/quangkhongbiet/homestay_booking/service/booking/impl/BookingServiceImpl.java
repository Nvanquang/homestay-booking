package vn.quangkhongbiet.homestay_booking.service.booking.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.PaymentStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
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
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HomestayRepository homestayRepository;
    private final HomestayAvailabilityService availabilityService;
    private final PriceService priceService;
    private final UserRepository userRepository;

    @Override
    public boolean existsById(long id) {
        return this.bookingRepository.existsById(id);
    }

    @Override
    public Booking book(ReqBooking request) {
        validateRequest(request);
        validateHomestay(request);

        final Long homestayId = request.getHomestayId();
        final LocalDate checkinDate = request.getCheckinDate();
        final LocalDate checkoutDate = request.getCheckoutDate();

        final var aDays = availabilityService.checkAvailabilityForBooking(homestayId, checkinDate, checkoutDate);

        final var price = priceService.calculate(aDays);
        final var booking = Booking.builder()
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
        if (request.getUserId() == null) {
            throw new BadRequestAlertException("ID user không được null!", "booking", "idnull");
        }

        if (!this.userRepository.existsById(request.getUserId())) {
            throw new BadRequestAlertException("ID user không tồn tại!", "booking", "usernotfound");
        }

        if (request.getHomestayId() == null) {
            throw new BadRequestAlertException("Homestay không được null!", "booking", "idnull");
        }

        if (!this.homestayRepository.existsById(request.getUserId())) {
            throw new BadRequestAlertException("Homestay không tồn tại!", "booking", "usernotfound");
        }

        if (checkinDate.isBefore(currentDate) || checkinDate.isAfter(checkoutDate)) {
            throw new BadRequestAlertException(ErrorConstants.CHECKIN_DATE_INVALID, "Ngày checkin không hợp lệ!", "booking", "checkininvalid");
        }

        if (request.getGuests() <= 0) {
            throw new BadRequestAlertException(ErrorConstants.GUESTS_INVALID, "Số lượng khách không hợp lệ!", "booking", "guestsinvalid");
        }
    }

    public void validateHomestay(ReqBooking request) {
        Homestay homestay = homestayRepository.findById(request.getHomestayId()).get();
        if (homestay == null) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Homestay không tồn tại!", "booking", "homestaynotfound");
        }

        if (homestay.getStatus() != HomestayStatus.ACTIVE) {
            throw new  BadRequestAlertException(ErrorConstants.HOMESTAY_NOT_ACTIVE, "Homestay không hoạt động!", "booking", "homestaynotactive");
        }

        if (homestay.getMaxGuests() < request.getGuests()) {
            throw new  BadRequestAlertException(ErrorConstants.GUESTS_INVALID, "Số lượng khách không hợp lệ!", "booking", "guestsinvalid");
        }
    }

    @Override
    public Optional<Booking> findBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public ResultPaginationDTO findAllBookings(Specification<Booking> spec, Pageable pageable) {
        Page<Booking> pageBookings = this.bookingRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = result.new Meta();

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
    public Optional<Booking> updatePartialBooking(Booking updatedBooking) {
        // update when nguoi dung thanh toan
        return this.bookingRepository.findById(updatedBooking.getId()).map(existingBooking -> {
            // Kiểm tra trạng thái booking trước khi cập nhật
            if (existingBooking.getStatus() == BookingStatus.CANCELLED) {
                throw new BadRequestAlertException("Không thể cập nhật booking đã bị hủy", "booking",
                        "bookingcancelled");
            }
            if (existingBooking.getStatus() == BookingStatus.COMPLETED) {
                throw new BadRequestAlertException("Không thể cập nhật booking đã hoàn tất", "booking",
                        "bookingcompleted");
            }
            if (updatedBooking.getStatus() != null) {
                // Kiểm tra chuyển trạng thái hợp lệ
                if (existingBooking.getStatus() == BookingStatus.BOOKED
                        && (updatedBooking.getStatus() != BookingStatus.COMPLETED
                        && updatedBooking.getStatus() != BookingStatus.CANCELLED)) {
                    throw new BadRequestAlertException(
                            "Trạng thái chỉ có thể chuyển từ BOOKED sang COMPLETED hoặc CANCELLED", "booking",
                            "bookingstatusinvalid");
                }
                existingBooking.setStatus(updatedBooking.getStatus());
            }
            if (updatedBooking.getPaymentStatus() != null) {
                // Kiểm tra trạng thái thanh toán hợp lệ
                if (existingBooking.getPaymentStatus() == PaymentStatus.COMPLETED
                        && updatedBooking.getPaymentStatus() == PaymentStatus.FAILED) {
                    throw new BadRequestAlertException("Không thể chuyển từ COMPLETED về FAILED", "booking",
                            "paymentstatusinvalid");
                }
                existingBooking.setPaymentStatus(updatedBooking.getPaymentStatus());
            }
            if (updatedBooking.getPaymentDate() != null && updatedBooking.getStatus() == BookingStatus.COMPLETED) {
                existingBooking.setPaymentDate(updatedBooking.getPaymentDate());
            }
            return existingBooking;
        }).map(this.bookingRepository::save);
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
                : new ResUser(0, null);
        builder.user(resUser);

        // convert Homestay to ResHomestay
        ResHomestay resHomestay = booking.getHomestay() != null ? new ResHomestay(booking.getHomestay().getId(),
                booking.getHomestay().getName(), booking.getHomestay().getAddress())
                : new ResHomestay(0, null, null);
        builder.homestay(resHomestay);
        return builder.build();
    }

}