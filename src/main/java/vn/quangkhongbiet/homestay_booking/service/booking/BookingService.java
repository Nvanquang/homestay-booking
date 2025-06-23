package vn.quangkhongbiet.homestay_booking.service.booking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingStatusDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResVnpBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface BookingService {
    Boolean existsById(Long id);

    ResVnpBookingDTO createBooking(ReqBooking request);

    Booking markBooked(Long bookingId);

    ResBookingStatusDTO getBookingStatus(Long id);

    Booking findBookingById(Long id);

    List<ResBookingDTO> findBookingByUser(Long userId);

    PagedResponse findAllBookings(Specification<Booking> spec, Pageable pageable);

    Booking updatePartialBooking(UpdateBookingDTO dto);

    ResBookingDTO convertToResBookingDTO(Booking booking);
}
