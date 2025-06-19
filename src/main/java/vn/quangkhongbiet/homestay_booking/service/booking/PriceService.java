package vn.quangkhongbiet.homestay_booking.service.booking;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.dto.BookingPrice;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.HomestayAvailability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final DiscountService discountService;
    
    private static final Logger log = LoggerFactory.getLogger(PriceService.class);

    public BookingPrice calculate(final List<HomestayAvailability> aDays) {
        log.debug("calculate Price with aDays: {}", aDays != null ? aDays.size() : 0);
        final var nights = aDays.size();
        var subtotal = BigDecimal.ZERO;

        for (var aDay: aDays) {
            subtotal = subtotal.add(aDay.getPrice());
        }

        final var discount = discountService.getDiscountAmount(subtotal, nights);

        return BookingPrice.builder()
                .subtotal(subtotal)
                .discount(discount)
                .totalAmount(subtotal.add(discount))
                .build();
    }
}
