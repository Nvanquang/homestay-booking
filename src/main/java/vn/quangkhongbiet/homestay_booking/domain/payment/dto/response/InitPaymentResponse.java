package vn.quangkhongbiet.homestay_booking.domain.payment.dto.response;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class InitPaymentResponse {
    private String vnpUrl;
}
