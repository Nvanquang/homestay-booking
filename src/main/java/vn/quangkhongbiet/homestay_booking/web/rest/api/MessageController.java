package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.request.SendMessageRequest;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.MessageResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.ConversationRepository;
import vn.quangkhongbiet.homestay_booking.service.chat.MessageService;
import vn.quangkhongbiet.homestay_booking.service.notification.firebase.FCMService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message management")
public class MessageController {

    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    private final FCMService fcmService;

    private final ConversationRepository conversationRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(SendMessageRequest request) {
        log.info("WebSocket request to send Message: {}", request);

        try {
            MessageResponse response = messageService.sendMessage(
                    request.getConversationId(),
                    request.getSenderId(),
                    request.getContent(),
                    request.getType());

            // Gửi tin nhắn đến topic
            messagingTemplate.convertAndSend(
                    "/topic/conversation." + request.getConversationId(),
                    response);

            Conversation conversation = this.conversationRepository.findById(request.getConversationId()).orElse(null);
            User receiver = new User();
            String title = "New messages from: ";
            if (conversation.getUser().getId().equals(request.getSenderId())) {
                // Người gửi là user, gửi thông báo cho host
                receiver = conversation.getHost();
                title += conversation.getUser().getFullName();
            } else {
                // Người gửi là host, gửi thông báo cho user
                receiver = conversation.getUser();
                title += conversation.getHost().getFullName();
            }

            // Send messages with FCM
            fcmService.sendSimple(receiver.getFcmToken(), title,
            response.getContent());


            log.info("Message sent successfully to conversation {}", request.getConversationId());

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            // Có thể gửi error message về client
            messagingTemplate.convertAndSend(
                    "/topic/conversation." + request.getConversationId() + ".error",
                    "Error: " + e.getMessage());
        }
    }
}
