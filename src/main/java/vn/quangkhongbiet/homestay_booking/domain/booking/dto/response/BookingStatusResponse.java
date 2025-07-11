package vn.quangkhongbiet.homestay_booking.domain.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookingStatusResponse {

    private Long bookingId;

    private Long userId;

    private Long homestayId;
    
    private BookingStatus status;
}

