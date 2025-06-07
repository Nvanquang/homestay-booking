package vn.quangkhongbiet.homestay_booking.domain.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResHomestay {
    private long id;
    private String name;
    private String address;
}
