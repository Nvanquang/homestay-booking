package vn.quangkhongbiet.homestay_booking.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Host ID is required")
    private Long hostId;
    
    @NotNull(message = "Homestay ID is required")
    private Long homestayId;

    private String firstMessage;
}
