package vn.quangkhongbiet.homestay_booking.web.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private int status;
    private String message;
    private Object data;
    private Instant timestamp;
}