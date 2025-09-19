package vn.quangkhongbiet.homestay_booking.service.booking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.CreateBookingRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.BookingResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.BookingStatusResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.VnpayBookingResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing bookings.
 * Provides methods for booking creation, update, status, and queries.
 */
public interface BookingService {
    /**
     * Check if a booking exists by id.
     * @param id the booking id.
     * @return true if exists, false otherwise.
     */
    Boolean existsById(Long id);

    /**
     * Create a new booking.
     * @param request the booking request DTO.
     * @return the response containing booking and payment info.
     * @throws EntityNotFoundException if user or homestay not found.
     * @throws BadRequestAlertException if request data invalid or homestay not active.
     */
    VnpayBookingResponse createBooking(CreateBookingRequest request);

    /**
     * Mark a booking as booked.
     * @param bookingId the booking id.
     * @return the updated booking entity.
     * @throws EntityNotFoundException if booking not found.
     */
    Booking markBooked(Long bookingId);

    /**
     * find booking status by id.
     * @param id the booking id.
     * @return the booking status DTO.
     * @throws EntityNotFoundException if booking not found.
     */
    BookingStatusResponse findBookingStatus(Long id);

    /**
     * Find a booking by id.
     * @param id the booking id.
     * @return the booking DTO.
     * @throws EntityNotFoundException if booking not found.
     */
    Booking findBookingById(Long id);

    /**
     * Find bookings by user id.
     * @param userId the user id.
     * @return list of booking DTOs.
     */
    List<BookingResponse> findBookingByUser(Long userId);

    /**
     * find all bookings with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of bookings.
     */
    PagedResponse findAllBookings(Specification<Booking> spec, Pageable pageable);

    /**
     * Update booking partially.
     * @param dto the update booking DTO.
     * @return the updated booking DTO.
     * @throws EntityNotFoundException if booking not found.
     * @throws BadRequestAlertException if booking is cancelled, completed, or status change invalid.
     */
    void updatePartialBooking(UpdateBookingRequest dto);

    /**
     * Convert booking entity to booking DTO.
     * @param booking the booking entity.
     * @return the booking DTO.
     */
    BookingResponse convertToResBookingDTO(Booking booking);
}
