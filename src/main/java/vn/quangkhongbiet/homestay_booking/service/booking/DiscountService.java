package vn.quangkhongbiet.homestay_booking.service.booking;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiscountService {

    private static final Integer LONG_STAY = 3;
    
    private static final BigDecimal LONG_STAY_DISCOUNT_RATE = BigDecimal.valueOf(-0.05);

    // Return a negative amount
    public BigDecimal getDiscountAmount(BigDecimal subtotal, Integer nights) {
        log.debug("get Discount amount with subtotal: {}, nights: {}", subtotal, nights);
        BigDecimal discount = BigDecimal.ZERO;

        if (nights >= LONG_STAY) {
            discount = subtotal.multiply(LONG_STAY_DISCOUNT_RATE);
        }

        return discount;
    }
}
