package vn.quangkhongbiet.homestay_booking.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotification {
    private String transactionId;
    private String status;
    private String message;
    private Long bookingId;
}
