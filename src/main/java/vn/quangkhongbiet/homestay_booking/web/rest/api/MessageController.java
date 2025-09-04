package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.request.SendMessageRequest;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.MessageResponse;
import vn.quangkhongbiet.homestay_booking.service.chat.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message management")
public class MessageController {
    
    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(SendMessageRequest request) { // Bỏ @RequestBody và @Valid
        log.info("WebSocket request to send Message: {}", request);
        
        try {
            MessageResponse response = messageService.sendMessage(
                request.getConversationId(),
                request.getSenderId(),
                request.getContent(),
                request.getType()
            );

            // Gửi tin nhắn đến topic
            messagingTemplate.convertAndSend(
                "/topic/conversation." + request.getConversationId(), 
                response
            );
            
            log.info("Message sent successfully to conversation {}", request.getConversationId());
            
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            // Có thể gửi error message về client
            messagingTemplate.convertAndSend(
                "/topic/conversation." + request.getConversationId() + ".error", 
                "Error: " + e.getMessage()
            );
        }
    }
}
