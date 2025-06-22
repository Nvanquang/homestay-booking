package vn.quangkhongbiet.homestay_booking.domain.payment.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpnResponse {

    private String responseCode;

    private String message;
}

