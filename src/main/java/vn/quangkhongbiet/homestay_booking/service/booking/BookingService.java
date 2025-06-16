package vn.quangkhongbiet.homestay_booking.service.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface BookingService {
    Boolean existsById(Long id);

    Booking createBooking(ReqBooking request);

    Booking findBookingById(Long id);

    PagedResponse findAllBookings(Specification<Booking> spec, Pageable pageable);

    Booking updatePartialBooking(UpdateBookingDTO dto);

    ResBookingDTO convertToResBookingDTO(Booking booking);
}
