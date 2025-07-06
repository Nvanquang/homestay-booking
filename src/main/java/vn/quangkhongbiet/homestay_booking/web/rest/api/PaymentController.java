package vn.quangkhongbiet.homestay_booking.web.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.entity.PaymentTransaction;
import vn.quangkhongbiet.homestay_booking.service.payment.VnpayIpnService;
import vn.quangkhongbiet.homestay_booking.service.payment.VnpayPaymentService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Quản lý thanh toán homestay")
public class PaymentController {

    private final VnpayIpnService ipnHandler;

    private final VnpayPaymentService paymentService;

    @GetMapping("/payments/vnpay_ipn")
    @Operation(summary = "Xử lý IPN từ VNPay", description = "Nhận và xử lý kết quả thanh toán từ VNPay gửi về theo chuẩn IPN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giao dịch được xử lý", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Thành công", value = "00|Giao dịch thành công"),
                    @ExampleObject(name = "Dữ liệu không hợp lệ", value = "01|Mã giao dịch không hợp lệ"),
                    @ExampleObject(name = "Lỗi giao dịch", value = "02|Giao dịch bị lỗi"),
                    @ExampleObject(name = "Lỗi số tiền", value = "04|Số tiền không hợp lệ"),
                    @ExampleObject(name = "Thiếu tham số", value = "97|Thiếu tham số trong yêu cầu"),
                    @ExampleObject(name = "Lỗi hệ thống", value = "99|Lỗi không xác định")
            }))
    })
    public ResponseEntity<String> processIpn(HttpServletRequest request) {
        log.info("[VNPay Ipn] request: {}", request);
        IpnResponse response = ipnHandler.process(request);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(response.getResponseCode() + "|" + response.getMessage());
    }

    @GetMapping("/payments/{id}")
    @ApiMessage("Lấy PaymentTransaction thành công")
    @Operation(summary = "Lấy PaymentTransaction theo ID", description = "Trả về PaymentTransaction theo ID cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy PaymentTransaction"),
            @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy PaymentTransaction")
    })
    public ResponseEntity<PaymentTransaction> getPaymentTransactionById(@PathVariable("id") Long id) {
        log.info("REST request to get PaymentTransaction by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Payment ID cannot be invalid", "payment", "idnull");
        }
        return ResponseEntity.ok(this.paymentService.findById(id));
    }

    @GetMapping("/payments")
    @ApiMessage("Lấy danh sách PaymentTransaction thành công")
    @Operation(summary = "Lấy danh sách PaymentTransaction", description = "Trả về danh sách PaymentTransaction có phân trang, lọc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllPaymentTransaction(
            @Filter Specification<PaymentTransaction> spec,
            Pageable pageable) {

        log.info("REST request to get all PaymentTransactions, pageable: {}", pageable);
        PagedResponse result = paymentService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

}
