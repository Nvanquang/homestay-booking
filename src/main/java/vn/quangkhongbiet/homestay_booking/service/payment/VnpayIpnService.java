package vn.quangkhongbiet.homestay_booking.service.payment;

import jakarta.servlet.http.HttpServletRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;

/**
 * Service interface for handling VNPay IPN (Instant Payment Notification).
 * Provides method for processing IPN requests from VNPay.
 */
public interface VnpayIpnService {
    /**
     * Process a VNPay IPN request.
     * @param request the HTTP servlet request containing IPN data.
     * @return the IPN response.
     */
    IpnResponse process(HttpServletRequest request);
}