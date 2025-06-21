package vn.quangkhongbiet.homestay_booking.service.payment;

import jakarta.servlet.http.HttpServletRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;

public interface VnpayIpnService {
    IpnResponse process(HttpServletRequest request);
}