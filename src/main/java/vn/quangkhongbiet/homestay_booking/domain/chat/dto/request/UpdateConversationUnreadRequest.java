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
public class UpdateConversationUnreadRequest {
    
    @NotNull(message = "Unread status is required")
    private Integer unreadCount;
}
