package vn.quangkhongbiet.homestay_booking.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpnResponse {
    @JsonProperty("RspCode")
    private String RspCode;

    @JsonProperty("Message")
    private String Message;
}

