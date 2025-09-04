package vn.quangkhongbiet.homestay_booking.service.chat;

import java.util.List;


import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageType;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.MessageResponse;

public interface MessageService {

    MessageResponse sendMessage(Long conversationId, Long senderId, String content, MessageType type);

    // void markMessageAsRead(Long messageId, Long userId);
    
    List<MessageResponse> getMessagesByConversation(Long conversationId, int page, int size);

    // CursorPageResponse<MessageResponse> getMessages(Long conversationId, Instant cursor, int page, int size);
}
