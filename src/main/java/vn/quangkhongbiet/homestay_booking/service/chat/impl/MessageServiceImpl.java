package vn.quangkhongbiet.homestay_booking.service.chat.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageStatus;
import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageType;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.MessageResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Message;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.ConversationRepository;
import vn.quangkhongbiet.homestay_booking.repository.MessageRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.chat.MessageService;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

        private static final String MESSAGE = "Message";

        private final MessageRepository messageRepository;

        private final ConversationRepository conversationRepository;

        private final UserRepository userRepository;

        @Override
        @Transactional
        public MessageResponse sendMessage(Long conversationId, Long senderId, String content, MessageType type) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(
                                                () -> new EntityNotFoundException("Conversation not found", MESSAGE,
                                                                "conversationnotfound"));
                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new EntityNotFoundException("Sender not found", MESSAGE,
                                                "sendernotfound"));

                Message message = Message.builder()
                                .conversation(conversation)
                                .sender(sender)
                                .content(type == MessageType.TEXT ? content : null)
                                .type(type)
                                .status(MessageStatus.SENT)
                                .timestamp(Instant.now())
                                .build();
                
                Message messageDb = messageRepository.save(message);

                conversation.setLastMessageId(messageDb.getId());
                conversation.setLastActivity(Instant.now());
                if (!sender.equals(conversation.getHost())) { // Nếu sender là guest
                        conversation.setUnreadCount(conversation.getUnreadCount());
                }
                conversation.getMessages().add(message);
                conversationRepository.save(conversation);

                
                MessageResponse response = MessageResponse.builder()
                                .id(messageDb.getId())
                                .senderId(messageDb.getSender().getId())
                                // .role(messageDb.getSender().getRole().getName().equals("USER") ? "guest": "host")
                                .content(messageDb.getContent())
                                .timestamp(messageDb.getTimestamp())
                                .type(message.getType())
                                .readAt(messageDb.getReadAt())
                                .build();
                return response;
        }

        // @Override
        // @Transactional
        // public void markMessageAsRead(Long messageId, Long userId) {
        //         Message message = messageRepository.findById(messageId)
        //                         .orElseThrow(() -> new EntityNotFoundException("Message not found", MESSAGE,
        //                                         "messagenotfound"));
        //         User user = userRepository.findById(userId)
        //                         .orElseThrow(() -> new EntityNotFoundException("Sender not found", MESSAGE,
        //                                         "sendernotfound"));

        //         if (message.getConversation().getHost().equals(user)
        //                         || message.getConversation().getUser().equals(user)) {
        //                 message.setStatus(MessageStatus.READ);
        //                 message.setReadAt(Instant.now());
        //                 messageRepository.save(message);

        //                 boolean allRead = message.getConversation().getMessages().stream()
        //                                 .allMatch(m -> m.getStatus() == MessageStatus.READ
        //                                                 || m.getSender().equals(user));

        //                 if (allRead) {
        //                         message.getConversation().setUnreadCount(0);
        //                         conversationRepository.save(message.getConversation());
        //                 }
        //         }
        // }

        @Override
        public List<MessageResponse> getMessagesByConversation(Long conversationId, int page, int size) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(
                                                () -> new EntityNotFoundException("Conversation not found", MESSAGE,
                                                                "conversationnotfound"));
                return messageRepository.findAllByConversation(conversation, PageRequest.of(page, size)).stream()
                                .map(this::mapToMessageResponse).collect(Collectors.toList());
        }

        // @Override
        // public CursorPageResponse<MessageResponse> getMessages(Long conversationId, Instant cursor, int page,
        //                 int size) {
        //         if (!conversationRepository.existsById(conversationId)) {
        //                 throw new EntityNotFoundException("Conversation not found", MESSAGE, "conversationnotfound");
        //         }

        //         List<MessageResponse> messages = messageRepository
        //                         .findMessages(conversationId, cursor, PageRequest.of(page, size)).stream()
        //                         .map(this::mapToMessageResponse).collect(Collectors.toList());

        //         Instant nextCursor = messages.isEmpty()
        //                         ? null
        //                         : messages.get(messages.size() - 1).getCreatedAt();

        //         return new CursorPageResponse<>(messages, nextCursor, !messages.isEmpty());
        // }

        private MessageResponse mapToMessageResponse(Message message) {
               
                return MessageResponse.builder()
                                .id(message.getId())
                                .senderId(message.getSender().getId())
                                .content(message.getContent())
                                .type(message.getType())
                                .timestamp(message.getTimestamp())
                                .readAt(message.getReadAt())
                                .build();
        }
}
