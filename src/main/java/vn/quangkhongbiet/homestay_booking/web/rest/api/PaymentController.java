package vn.quangkhongbiet.homestay_booking.web.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.PaymentNotification;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management")
public class PaymentController {

    private final VnpayIpnService ipnHandler;

    private final VnpayPaymentService paymentService;

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/payments/vnpay_ipn")
    public ResponseEntity<IpnResponse> processIpn(HttpServletRequest request) {
        log.info("[VNPay Ipn] request: {}", request);

        IpnResponse response = ipnHandler.process(request);
        if (!response.getRspCode().equals("00")) {
            PaymentNotification notification = PaymentNotification.builder()
                    .transactionId(null)
                    .status("faild")
                    .message("Thanh toán không thành công! Vui lòng thử lại sau.")
                    .bookingId(null)
                    .build();

            messagingTemplate.convertAndSend(
                    "/topic/payments." + request.getParameter("vnp_TxnRef"), notification);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping("/payments/{id}")
    @ApiMessage("Get PaymentTransaction successfully")
    public ResponseEntity<PaymentTransaction> getPaymentTransactionById(@PathVariable("id") Long id) {
        log.info("REST request to get PaymentTransaction by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Payment ID cannot be invalid", "payment", "idnull");
        }
        return ResponseEntity.ok(this.paymentService.findById(id));
    }

    @GetMapping("/payments")
    @ApiMessage("Get PaymentTransaction list successfully")
    public ResponseEntity<PagedResponse> getAllPaymentTransaction(
            @Filter Specification<PaymentTransaction> spec,
            Pageable pageable) {

        log.info("REST request to get all PaymentTransactions, pageable: {}", pageable);
        PagedResponse result = paymentService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

}
