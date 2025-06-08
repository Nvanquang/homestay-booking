package vn.quangkhongbiet.homestay_booking.service.booking;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface BookingService {
    Boolean existsById(Long id);

    Booking book(ReqBooking request);

    Optional<Booking> findBookingById(Long id);

    ResultPaginationDTO findAllBookings(Specification<Booking> spec, Pageable pageable);

    Optional<Booking> updatePartialBooking(Booking booking);

    ResBookingDTO convertToResBookingDTO(Booking booking);
}
