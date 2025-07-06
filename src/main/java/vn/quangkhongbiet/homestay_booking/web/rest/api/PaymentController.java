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
@Tag(name = "Payment", description = "Payment management")
public class PaymentController {

    private final VnpayIpnService ipnHandler;

    private final VnpayPaymentService paymentService;

    @GetMapping("/payments/vnpay_ipn")
    @Operation(summary = "Process IPN from VNPay", description = "Receive and process payment result from VNPay via IPN standard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction processed", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = {
                    @ExampleObject(name = "Success", value = "00|Transaction successful"),
                    @ExampleObject(name = "Invalid data", value = "01|Invalid transaction code"),
                    @ExampleObject(name = "Transaction error", value = "02|Transaction error"),
                    @ExampleObject(name = "Invalid amount", value = "04|Invalid amount"),
                    @ExampleObject(name = "Missing parameter", value = "97|Missing parameter in request"),
                    @ExampleObject(name = "System error", value = "99|Unknown error")
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
    @ApiMessage("Get PaymentTransaction successfully")
    @Operation(summary = "Get PaymentTransaction by ID", description = "Return PaymentTransaction by specific ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PaymentTransaction found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID"),
            @ApiResponse(responseCode = "404", description = "PaymentTransaction not found")
    })
    public ResponseEntity<PaymentTransaction> getPaymentTransactionById(@PathVariable("id") Long id) {
        log.info("REST request to get PaymentTransaction by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Payment ID cannot be invalid", "payment", "idnull");
        }
        return ResponseEntity.ok(this.paymentService.findById(id));
    }

    @GetMapping("/payments")
    @ApiMessage("Get PaymentTransaction list successfully")
    @Operation(summary = "Get PaymentTransaction list", description = "Return paginated and filtered PaymentTransaction list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<PagedResponse> getAllPaymentTransaction(
            @Filter Specification<PaymentTransaction> spec,
            Pageable pageable) {

        log.info("REST request to get all PaymentTransactions, pageable: {}", pageable);
        PagedResponse result = paymentService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

}
