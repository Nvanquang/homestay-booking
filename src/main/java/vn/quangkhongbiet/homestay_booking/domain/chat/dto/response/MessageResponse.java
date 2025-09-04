package vn.quangkhongbiet.homestay_booking.domain.chat.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageType;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    // private String role;
    private String content;
    private MessageType type;
    private Instant timestamp;
    private Instant readAt;
}
