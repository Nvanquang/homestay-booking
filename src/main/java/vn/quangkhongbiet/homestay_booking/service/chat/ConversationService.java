package vn.quangkhongbiet.homestay_booking.service.chat;

import java.util.List;
import java.util.Optional;

import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.ConversationResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;

public interface ConversationService {

    Conversation createConversation(Long userId, Long hostId, Long homestayId, String firstMessage);

    List<ConversationResponse> getConversationsByUser(Long userId);

    Optional<ConversationResponse> getConversationById(Long conversationId);

    void updateConversationUnreadCount(Long conversationId, Integer unreadCount);
    
    void deleteConversation(Long conversationId, Long userId);
}
