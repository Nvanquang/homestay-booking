package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqHomestaySearch {
    @NotNull(message = "Kinh độ không được để trống")
    @DecimalMin(value = "-180.0", message = "Kinh độ phải từ -180 trở lên")
    @DecimalMax(value = "180.0", message = "Kinh độ phải từ 180 trở xuống")
    private Double longitude;

    @NotNull(message = "Vĩ độ không được để trống")
    @DecimalMin(value = "-90.0", message = "Vĩ độ phải từ -90 trở lên")
    @DecimalMax(value = "90.0", message = "Vĩ độ phải từ 90 trở xuống")
    private Double latitude;

    @NotNull(message = "Bán kính không được để trống")
    @Positive(message = "Bán kính phải là số dương")
    @DecimalMax(value = "100000.0", message = "Bán kính không được vượt quá 100km")
    private Double radius;

    @NotNull(message = "Ngày nhận phòng không được để trống")
    @FutureOrPresent(message = "Ngày nhận phòng phải là hôm nay hoặc trong tương lai")
    private LocalDate checkinDate;

    @NotNull(message = "Ngày trả phòng không được để trống")
    @FutureOrPresent(message = "Ngày trả phòng phải là hôm nay hoặc trong tương lai")
    private LocalDate checkoutDate;

    @NotNull(message = "Số lượng khách không được để trống")
    @Positive(message = "Số lượng khách phải là số dương")
    @Max(value = 20, message = "Số lượng khách không được vượt quá 20")
    private Integer guests;

    private AvailabilityStatus status;
}
