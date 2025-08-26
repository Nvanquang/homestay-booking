package vn.quangkhongbiet.homestay_booking.domain.payment.constant;

import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.IpnResponse;

public class VnpIpnResponseConst {
    
    public static final IpnResponse SUCCESS = new IpnResponse("00", "Confirm Success");
    public static final IpnResponse ORDER_NOT_FOUND = new IpnResponse("01", "Order not Found");
    public static final IpnResponse PAYMENT_FAILED = new IpnResponse("02", "Order already confirmed");
    public static final IpnResponse INVALID_AMOUNT = new IpnResponse("04", "Invalid Amount");
    public static final IpnResponse SIGNATURE_FAILED = new IpnResponse("97", "Invalid Checksum");
    public static final IpnResponse UNKNOWN_ERROR = new IpnResponse("99", "Unknow error");
}
