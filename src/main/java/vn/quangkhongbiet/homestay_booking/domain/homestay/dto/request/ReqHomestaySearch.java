package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqHomestaySearch {
    private Double longitude;
    private Double latitude;
    private Double radius;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer guests;
    private AvailabilityStatus status;
}
