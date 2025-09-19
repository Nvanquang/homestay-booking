package vn.quangkhongbiet.homestay_booking.domain.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long id;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private Integer guests;

    private BookingStatus status;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal totalAmount;

    private String note;

    private ResUser user;

    private ResHomestay homestay;

    private Boolean isReviewed;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResUser {
        private Long id;
        private String fullName;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResHomestay {
        private Long id;
        private String name;
        private String address;
    }

}
