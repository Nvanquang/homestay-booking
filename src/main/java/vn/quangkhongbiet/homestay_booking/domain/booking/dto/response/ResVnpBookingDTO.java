package vn.quangkhongbiet.homestay_booking.domain.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.InitPaymentResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResVnpBookingDTO {
    
    ResBookingDTO booking;

    InitPaymentResponse payment;
}