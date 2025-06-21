package vn.quangkhongbiet.homestay_booking.service.payment.impl;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.payment.constant.VNPayParams;
import vn.quangkhongbiet.homestay_booking.domain.payment.constant.VnpIpnResponseConst;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.entity.PaymentTransaction;
import vn.quangkhongbiet.homestay_booking.repository.PaymentTransactionRepository;
import vn.quangkhongbiet.homestay_booking.service.booking.BookingService;
import vn.quangkhongbiet.homestay_booking.service.payment.VnpayIpnService;
import vn.quangkhongbiet.homestay_booking.utils.VnpayUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class VnpayIpnServiceImpl implements VnpayIpnService {

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;

    private final BookingService bookingService;

    private final PaymentTransactionRepository paymentRepository;

    @Override
    public IpnResponse process(HttpServletRequest request) {
        log.debug("[VNPay Ipn] request with raw parameters: {}", request.getQueryString());
        try {
            Map<String, String> params = extractFields(request);
            String secureHash = request.getParameter("vnp_SecureHash");
      
            if (!validateChecksum(params, secureHash)) {
                return VnpIpnResponseConst.SIGNATURE_FAILED;
            }

            return handleValidRequest(params);
        } catch (Exception e) {
            return VnpIpnResponseConst.UNKNOWN_ERROR;
        }
    }

    private Map<String, String> extractFields(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            if (!name.equals("vnp_SecureHash") && !name.equals("vnp_SecureHashType")) {
                fields.put(name, request.getParameter(name));
            }
        }
        log.debug("[VNPay Ipn] fields for checksum validation: {}", fields);
        return fields;
    }
   

    private boolean validateChecksum(Map<String, String> fields, String receivedHash) {
        String calculatedHash = VnpayUtil.hashAllFields(secretKey, fields);
        log.debug("[VNPay Ipn] received hash: {}", receivedHash);
        log.debug("[VNPay Ipn] calculated hash: {}", calculatedHash);
        return calculatedHash.equals(receivedHash);
    }

    private IpnResponse handleValidRequest(Map<String, String> fields) {
        String txnRef = fields.get("vnp_TxnRef");
        String vnpAmount = fields.get("vnp_Amount");
        String responseCode = fields.get("vnp_ResponseCode");

        log.debug("[VNPay Ipn] handling request for vnp_TxnRef: {}", txnRef);
        log.debug("[VNPay Ipn] booking total amount (x100): {}", vnpAmount);
        log.debug("[VNPay Ipn] response code: {}", responseCode);

        Booking booking = bookingService.findBookingById(Long.parseLong(txnRef));
        if (booking == null) return VnpIpnResponseConst.ORDER_NOT_FOUND;

        String amountStr = booking.getTotalAmount()
                .multiply(BigDecimal.valueOf(100)) // VNPAY nhân 100 lần
                .toBigInteger().toString();

        if (!amountStr.equals(vnpAmount))
            return VnpIpnResponseConst.INVALID_AMOUNT;

        if (!booking.getStatus().equals(BookingStatus.PAYMENT_PROCESSING))
            return VnpIpnResponseConst.PAYMENT_FAILED;

        if ("00".equals(responseCode)) {
            booking.setStatus(BookingStatus.COMPLETED);
            
        } else {
            booking.setStatus(BookingStatus.PAYMENT_FAILED);
        }

        bookingService.markBooked(booking.getId());
        log.debug("[VNPay Ipn] saving PaymentTransaction for bookingId: {}", booking.getId());
        buildPaymentTransaction(fields, booking);
        return VnpIpnResponseConst.SUCCESS;
    }

    private void buildPaymentTransaction(Map<String, String> params, Booking booking){
        PaymentTransaction payment = PaymentTransaction.builder()
            .transactionId(params.get(VNPayParams.TRANSACTION_NO))
            .status(params.get(VNPayParams.RESPONSE_CODE))
            .amount(new BigDecimal(params.get(VNPayParams.AMOUNT)))
            .responseMessage(params.get(VNPayParams.ORDER_INFO))
            .requestId(booking.getRequestId())
            .createdAt(java.time.Instant.now())
            .booking(booking)
            .build();
        
        log.debug("[VNPay Ipn] saving PaymentTransaction for bookingId: {}, transactionId: {}, amount: {}",
            booking.getId(),
            payment.getTransactionId(),
            payment.getAmount());
        
        this.paymentRepository.save(payment);
            
    }

}