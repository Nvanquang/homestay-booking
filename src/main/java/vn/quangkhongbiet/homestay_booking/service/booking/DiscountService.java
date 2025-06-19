package vn.quangkhongbiet.homestay_booking.service.booking;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);

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
