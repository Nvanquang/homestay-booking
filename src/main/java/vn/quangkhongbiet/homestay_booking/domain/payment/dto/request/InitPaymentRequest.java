package vn.quangkhongbiet.homestay_booking.domain.payment.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InitPaymentRequest {

    private String requestId;

    private String ipAddress;

    private Long userId;

    private String txnRef;

    private long amount;

}
