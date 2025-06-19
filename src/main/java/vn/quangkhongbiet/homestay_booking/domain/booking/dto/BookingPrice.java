package vn.quangkhongbiet.homestay_booking.domain.booking.dto;

import java.math.BigDecimal;

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
public class BookingPrice {
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal totalAmount;
}
