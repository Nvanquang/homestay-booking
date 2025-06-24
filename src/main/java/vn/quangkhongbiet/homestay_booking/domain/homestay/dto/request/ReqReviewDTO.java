package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqReviewDTO {
    
    @NotNull
    @Min(value = 1, message = "Thấp nhất là 1 sao")
    @Max(value = 5, message = "Cao nhất là 5 sao")
    private Integer rating;

    private String comment;

    @NotNull
    private Long homestayId;
}
