package vn.quangkhongbiet.homestay_booking.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallSignalMessageRequest {
  private Long conversationId;
  private String fromUserId;
  private String toUserId; // optional, 1-1 có thể không cần nếu route theo conversation
  private String type; // OFFER, ANSWER, CANDIDATE, CALL_INIT, CALL_ACCEPT, CALL_REJECT, CALL_END
  private Object payload; // SDP/ICE JSON
  private Boolean isVideo;
  private Long timestamp;
}
