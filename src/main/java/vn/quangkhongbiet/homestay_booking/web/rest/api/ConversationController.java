package vn.quangkhongbiet.homestay_booking.web.rest.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.request.CreateConversationRequest;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.request.UpdateConversationUnreadRequest;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.request.DeleteConversationRequest;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.ConversationResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.CreateConversationResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.MessageResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.service.chat.ConversationService;
import vn.quangkhongbiet.homestay_booking.service.chat.MessageService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Conversation", description = "Conversation management")
public class ConversationController {
    
    private static final String ENTITY_NAME = "conversation";

    private final ConversationService conversationService;

    private final MessageService messageService;

    @PostMapping("/conversations")
    @ApiMessage("Conversation created successfully")
    public ResponseEntity<CreateConversationResponse> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        log.info("REST request to create Conversation: {}", request);
        
        Conversation conversation = conversationService.createConversation(
            request.getUserId(), 
            request.getHostId(), 
            request.getHomestayId()
        );
        
        CreateConversationResponse response = new CreateConversationResponse(
            conversation.getId(), 
            "Conversation created successfully"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversations/user/{userId}")
    @ApiMessage("Get conversations by user successfully")
    public ResponseEntity<List<ConversationResponse>> getConversationsByUser(@PathVariable("userId") Long userId) {
        log.info("REST request to get Conversations by user: {}", userId);
        
        if (userId <= 0) {
            throw new BadRequestAlertException("Invalid User ID", ENTITY_NAME, "invaliduserid");
        }
        
        List<ConversationResponse> responses = conversationService.getConversationsByUser(userId);
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/conversations/{id}")
    @ApiMessage("Get conversation by ID successfully")
    public ResponseEntity<ConversationResponse> getConversationById(@PathVariable("id") Long id) {
        log.info("REST request to get Conversation by id: {}", id);
        
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "invalidid");
        }
        
        return ResponseEntity.ok(conversationService.getConversationById(id).orElse(null));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @ApiMessage("Get messages by conversation successfully")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable("conversationId") Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("REST request to get Messages by conversation: {}, page: {}, size: {}", conversationId, page, size);
        
        if (conversationId <= 0) {
            throw new BadRequestAlertException("Invalid Conversation ID", ENTITY_NAME, "invalidconversationid");
        }
        
        if (page < 0) {
            throw new BadRequestAlertException("Page number cannot be negative", ENTITY_NAME, "invalidpage");
        }
        
        if (size <= 0 || size > 100) {
            throw new BadRequestAlertException("Size must be between 1 and 100", ENTITY_NAME, "invalidsize");
        }
        
        List<MessageResponse> responses = this.messageService.getMessagesByConversation(conversationId, page, size);
        
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/conversations/{id}/unread")
    @ApiMessage("Conversation unread status updated successfully")
    public ResponseEntity<Void> updateConversationUnreadStatus(
            @PathVariable("id") Long id, 
            @Valid @RequestBody UpdateConversationUnreadRequest request) {
        log.info("REST request to update Conversation unreadCount status, id: {}, unreadCount: {}", id, request.getUnreadCount());
        
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "invalidid");
        }
        
        conversationService.updateConversationUnreadCount(id, request.getUnreadCount());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conversations/{id}")
    @ApiMessage("Conversation deleted successfully")
    public ResponseEntity<Void> deleteConversation(@PathVariable("id") Long id, @Valid @RequestBody DeleteConversationRequest request) {
        log.info("REST request to delete Conversation by id: {}, userId: {}", id, request.getUserId());
        
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "invalidid");
        }
        
        if (request.getUserId() <= 0) {
            throw new BadRequestAlertException("Invalid User ID", ENTITY_NAME, "invaliduserid");
        }
        
        conversationService.deleteConversation(id, request.getUserId());
        return ResponseEntity.ok().build();
    }

    
}
