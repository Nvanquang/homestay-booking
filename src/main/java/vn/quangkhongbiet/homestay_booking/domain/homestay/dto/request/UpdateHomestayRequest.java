package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomestayRequest {
    
    @NotNull(message = "Homestay ID is required")
    @Positive(message = "Homestay ID must be positive")
    private Long id;

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    private HomestayStatus status;

    private Long hostId;
    
    @Positive(message = "Number of guests must be positive")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer guests;

    private List<Long> amenities;

    private List<String> deletedImages;
}
