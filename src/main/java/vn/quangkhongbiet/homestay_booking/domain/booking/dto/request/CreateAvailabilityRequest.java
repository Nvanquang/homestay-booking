package vn.quangkhongbiet.homestay_booking.domain.booking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAvailabilityRequest {

    private Long homestayId;

    private List<LocalDate> dates;

    private BigDecimal price;

    private AvailabilityStatus status;
}
