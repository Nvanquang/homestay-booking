package vn.quangkhongbiet.homestay_booking.domain.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayInitPaymentRequest {

    public static final String VERSION = "2.1.0";

    public static final String COMMAND = "pay";

    private String requestId;

    private String tmnCode;

    private String txnRef;

    private String createdDate;

    private String ipAddress;

    private String orderInfo;

    private String secureHash;
}
