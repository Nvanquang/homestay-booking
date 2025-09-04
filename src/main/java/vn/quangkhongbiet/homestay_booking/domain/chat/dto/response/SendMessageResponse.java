package vn.quangkhongbiet.homestay_booking.domain.chat.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String message;
    private Instant createdAt;
}
