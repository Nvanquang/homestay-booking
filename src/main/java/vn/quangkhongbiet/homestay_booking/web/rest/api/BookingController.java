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
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.ReqBooking;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.request.UpdateBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResBookingStatusDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.response.ResVnpBookingDTO;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.utils.VnpayUtil;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Booking", description = "Quản lý đặt phòng homestay")
public class BookingController {
    
    private static final String ENTITY_NAME = "booking";

    private final BookingService bookingService;

    private final UserService userService;

    @PostMapping("/bookings")
    @ApiMessage("Đặt phòng thành công")
    @Operation(summary = "Tạo booking", description = "Tạo mới một booking trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
    public ResponseEntity<ResVnpBookingDTO> createBooking(
        @Valid @RequestBody ReqBooking request, 
        HttpServletRequest httpServletRequest) {

        String ipAddress = VnpayUtil.getIpAddress(httpServletRequest);
        request.setIpAddress(ipAddress);

        log.info("REST request to create Booking: {}", request);
        ResVnpBookingDTO createdBooking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @GetMapping("/bookings/{id}")
    @ApiMessage("Lấy thông tin đặt phòng thành công")
    @Operation(summary = "Lấy booking theo ID", description = "Trả về booking theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy booking"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy booking")
    })
    public ResponseEntity<ResBookingDTO> getBookingById(@PathVariable("id") Long id) {
        log.info("REST request to get Booking by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(this.bookingService.convertToResBookingDTO(bookingService.findBookingById(id)));
    }

    @GetMapping("/bookings/history/{userId}")
    @ApiMessage("Lấy lịch sử đặt phòng thành công")
    @Operation(summary = "Lấy lịch sử booking theo user", description = "Trả về lịch sử booking của user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    public ResponseEntity<List<ResBookingDTO>> getBookingHistory(@PathVariable("userId") Long userId) {
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
    @ApiMessage("Lấy trạng thái đặt phòng thành công")
    @Operation(summary = "Lấy trạng thái booking", description = "Trả về trạng thái booking theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy booking")
    })
    public ResponseEntity<ResBookingStatusDTO> getBookingStatus(@PathVariable("id") Long id) {
        log.info("REST request to get Booking status by id: {}", id);
        if(id <= 0){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idinvalid");
        }
        return ResponseEntity.ok(this.bookingService.getBookingStatus(id));
    }

    @GetMapping("/bookings")
    @ApiMessage("Lấy danh sách đặt phòng thành công")
    @Operation(summary = "Lấy danh sách booking", description = "Trả về danh sách booking có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllBookings(@Filter Specification<Booking> spec, Pageable pageable) {
        log.info("REST request to get all Bookings, pageable: {}", pageable);
        return ResponseEntity.ok(bookingService.findAllBookings(spec, pageable));
    }

    @PatchMapping("/bookings/{id}")
    @ApiMessage("Cập nhật thông tin đặt phòng thành công")
    @Operation(summary = "Cập nhật booking", description = "Cập nhật thông tin booking theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy booking"),
        @ApiResponse(responseCode = "500", description = "Không thể cập nhật booking")
    })
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
