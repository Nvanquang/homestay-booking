package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.CreateBookingRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingRequest;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.BookingResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.BookingStatusResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.VnpayBookingResponse;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.utils.VnpayUtil;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Booking", description = "Booking management")
public class BookingController {
    
    private static final String ENTITY_NAME = "booking";

    private final BookingService bookingService;

    private final UserService userService;

    @PostMapping("/bookings")
    @ApiMessage("Booking created successfully")
    public ResponseEntity<VnpayBookingResponse> createBooking(
        @Valid @RequestBody CreateBookingRequest request, 
        HttpServletRequest httpServletRequest) {

        String ipAddress = VnpayUtil.getIpAddress(httpServletRequest);
        request.setIpAddress(ipAddress);

        log.info("REST request to create Booking: {}", request);
        VnpayBookingResponse createdBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @GetMapping("/bookings/{id}")
    @ApiMessage("Get booking information successfully")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable("id") Long id) {
        log.info("REST request to get Booking by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        BookingResponse resBookingDTO = this.bookingService.convertToResBookingDTO(this.bookingService.findBookingById(id));
        return ResponseEntity.ok(resBookingDTO);
    }

    @GetMapping("/bookings/history/{userId}")
    @ApiMessage("Get booking history successfully")
    public ResponseEntity<List<BookingResponse>> getBookingHistory(@PathVariable("userId") Long userId) {
        log.info("REST request to get Booking History by userId: {}", userId);
        if(userId <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if(!this.userService.existsById(userId)){
            throw new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound");
        }
        return ResponseEntity.ok(this.bookingService.findBookingByUser(userId));
    }

    @GetMapping("/bookings/{id}/status")
    @ApiMessage("Get booking status successfully")
    public ResponseEntity<BookingStatusResponse> getBookingStatus(@PathVariable("id") Long id) {
        log.info("REST request to get Booking status by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(this.bookingService.findBookingStatus(id));
    }

    @GetMapping("/bookings")
    @ApiMessage("Get booking list successfully")
    public ResponseEntity<PagedResponse> getAllBookings(@Filter Specification<Booking> spec, Pageable pageable) {
        log.info("REST request to get all Bookings, pageable: {}", pageable);
        return ResponseEntity.ok(bookingService.findAllBookings(spec, pageable));
    }

    @PatchMapping("/bookings/{id}")
    @ApiMessage("Booking updated successfully")
    public ResponseEntity<Void> updatePartialBooking(@PathVariable("id") Long id, @Valid @RequestBody UpdateBookingRequest dto) {
        log.info("REST request to update Booking partially, id: {}, body: {}", id, dto);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idmismatch");
        }
        this.bookingService.updatePartialBooking(dto);
        return ResponseEntity.ok(null);
    }
}
