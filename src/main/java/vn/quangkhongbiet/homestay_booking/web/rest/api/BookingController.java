package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookingController {

    private static final String ENTITY_NAME = "booking";

    private BookingService bookingService;

    @PostMapping("/bookings")
    @ApiMessage("Đặt phòng thành công")
    public ResponseEntity<?> bookHomestay(@Valid @RequestBody ReqBooking request) {
        if (request == null) {
            throw new BadRequestAlertException("Dữ liệu đặt phòng không được null", ENTITY_NAME, "bookingnull");
        }   
        Booking createdBooking = bookingService.book(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bookingService.convertToResBookingDTO(createdBooking));
    }

    @GetMapping("/bookings/{id}")
    @ApiMessage("Lấy thông tin đặt phòng thành công")
    public ResponseEntity<ResBookingDTO> getBookingById(@PathVariable("id") Long id) {
        if (id == null) {
            throw new BadRequestAlertException("ID đặt phòng không được null", ENTITY_NAME, "idnull");
        }
        if (!bookingService.existsById(id)) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy đặt phòng với ID " + id, ENTITY_NAME, "bookingnotfound");
        }
        return ResponseEntity.ok(this.bookingService.convertToResBookingDTO(bookingService.findBookingById(id).get()));
    }

    @GetMapping("/bookings")
    @ApiMessage("Lấy danh sách đặt phòng thành công")
    public ResponseEntity<ResultPaginationDTO> getAllBookings(@Filter Specification<Booking> spec, Pageable pageable) {
        return ResponseEntity.ok(bookingService.findAllBookings(spec, pageable));
    }

    @PatchMapping("/bookings")
    @ApiMessage("Cập nhật thông tin đặt phòng thành công")
    public ResponseEntity<?> updatePartialBooking(@Valid @RequestBody Booking booking) {
        if (booking.getId() == null) {
            throw new BadRequestAlertException("ID đặt phòng không được null", ENTITY_NAME, "idnull");
        }
        if (!bookingService.existsById(booking.getId())) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy đặt phòng với ID " + booking.getId(), ENTITY_NAME, "bookingnotfound");
        }
        Booking updatedBooking = bookingService.updatePartialBooking(booking).get();
        return ResponseEntity.ok(updatedBooking);
    }
}
