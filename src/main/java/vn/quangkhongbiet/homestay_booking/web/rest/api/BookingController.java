package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    
    private static final String ENTITY_NAME = "booking";

    private final BookingService bookingService;

    @PostMapping("/bookings")
    @ApiMessage("Đặt phòng thành công")
    public ResponseEntity<ResBookingDTO> createBooking(@Valid @RequestBody ReqBooking request) {
        log.info("REST request to create Booking: {}", request);
        Booking createdBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.bookingService.convertToResBookingDTO(createdBooking));
    }

    @GetMapping("/bookings/{id}")
    @ApiMessage("Lấy thông tin đặt phòng thành công")
    public ResponseEntity<ResBookingDTO> getBookingById(@PathVariable("id") Long id) {
        log.info("REST request to get Booking by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(this.bookingService.convertToResBookingDTO(bookingService.findBookingById(id)));
    }

    @GetMapping("/bookings")
    @ApiMessage("Lấy danh sách đặt phòng thành công")
    public ResponseEntity<PagedResponse> getAllBookings(@Filter Specification<Booking> spec, Pageable pageable) {
        log.info("REST request to get all Bookings, pageable: {}", pageable);
        return ResponseEntity.ok(bookingService.findAllBookings(spec, pageable));
    }

    @PatchMapping("/bookings/{id}")
    @ApiMessage("Cập nhật thông tin đặt phòng thành công")
    public ResponseEntity<?> updatePartialBooking(@PathVariable("id") Long id, @Valid @RequestBody UpdateBookingDTO dto) {
        log.info("REST request to update Booking partially, id: {}, body: {}", id, dto);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idmismatch");
        }
        Booking updatedBooking = bookingService.updatePartialBooking(dto);
        return ResponseEntity.ok(this.bookingService.convertToResBookingDTO(updatedBooking));
    }
}
