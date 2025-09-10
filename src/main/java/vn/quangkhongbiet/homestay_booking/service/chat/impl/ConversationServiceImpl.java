package vn.quangkhongbiet.homestay_booking.service.chat.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageStatus;
import vn.quangkhongbiet.homestay_booking.domain.chat.constant.MessageType;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.ConversationResponse;
import vn.quangkhongbiet.homestay_booking.domain.chat.dto.response.ConversationResponse.UserInfo;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Message;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.ConversationRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.MessageRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.chat.ConversationService;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private static final String CONVERSATION = "Conversation";

    private final ConversationRepository conversationRepository;

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final HomestayRepository homestayRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
        public Conversation createConversation(Long userId, Long hostId, Long homestayId, String message) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found", CONVERSATION, "usernotfound"));
            User host = userRepository.findById(hostId)
                    .orElseThrow(() -> new EntityNotFoundException("Host not found", CONVERSATION, "usernotfound"));

            Conversation existingConversation = conversationRepository.findByUserAndHostAndHomestayId(user, host, homestayId);
            if (existingConversation != null) {
                return existingConversation;
            }

            Conversation conversation = new Conversation();
            conversation.setUser(user);
            conversation.setHost(host);
            conversation.setHomestayId(homestayId);
            conversation.setUnreadCount(0);
            conversation.setCreatedAt(Instant.now());
            conversation.setMessages(new ArrayList<>());

            Conversation conversationDb = conversationRepository.save(conversation);

            
            Message firstmessage = Message.builder()
            .sender(user)
            .content(message)
            .type(MessageType.TEXT)
            .status(MessageStatus.SENT)
            .readAt(null)
            .timestamp(Instant.now())
            .conversation(conversationDb)
            .build();
            Long firstMessageId = this.messageRepository.save(firstmessage).getId();

            conversationDb.getMessages().add(firstmessage);
            conversationDb.setLastMessageId(firstMessageId);
            conversationDb.setLastActivity(firstmessage.getTimestamp());

             messagingTemplate.convertAndSend(
                "/topic/conversation." + conversationDb.getId(), 
                firstmessage
            );

            return conversationDb;

        }

    @Override
    public List<ConversationResponse> getConversationsByUser(Long userId) {
        if (!this.userRepository.existsById(userId)){
            throw new EntityNotFoundException("User not found", CONVERSATION, "usernotfound");
        }
        return conversationRepository.findByUserOrHost(userId).stream().map(this::mapToConversationResponse).collect(Collectors.toList());
    }

    @Override
    public Optional<ConversationResponse> getConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId).map(this::mapToConversationResponse);
    }

    @Override
    @Transactional
    public void updateConversationUnreadCount(Long conversationId, Integer unreadCount) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found", CONVERSATION, "conversationnotfound"));
        conversation.setUnreadCount(unreadCount);
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found", CONVERSATION, "conversationnotfound"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", CONVERSATION, "usernotfound"));

        if (conversation.getUser().equals(user) || conversation.getHost().equals(user)) {
            conversationRepository.delete(conversation);
        } else {
            throw new UnauthorizedException("User not authorized to delete this conversation", CONVERSATION, "usernotauthorized");
        }
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation) {
        ConversationResponse.UserInfo userInfo = null;
        if (conversation.getUser() != null) {
            userInfo = ConversationResponse.UserInfo.builder()
                .id(conversation.getUser().getId())
                .fullName(conversation.getUser().getFullName())
                .avatar(conversation.getUser().getAvatar())
                .role("guest")
                .build();
        }

        ConversationResponse.UserInfo hostInfo = null;
        if (conversation.getHost() != null) {
            hostInfo = ConversationResponse.UserInfo.builder()
                .id(conversation.getHost().getId())
                .fullName(conversation.getHost().getFullName())
                .avatar(conversation.getHost().getAvatar())
                .role("host")
                .build();
        }

        Message message = this.messageRepository.findById(conversation.getLastMessageId()).get();
        ConversationResponse.MessageRes lastMessage = ConversationResponse.MessageRes.builder()
            .id(message.getId())
            .content(message.getContent())
            .senderId(message.getSender().getId())
            .type(message.getType())
            .timestamp(message.getTimestamp())
            .build();

        List<UserInfo> participants = new ArrayList<>();
        participants.add(userInfo);
        participants.add(hostInfo);

        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        User currentUser = this.userRepository.findByEmail(email).get();

        Homestay homestay = this.homestayRepository.findById(conversation.getHomestayId()).orElseThrow(() -> new EntityNotFoundException("Homestay not found", CONVERSATION, "homestaynotfound"));

        ConversationResponse.HomestayInfo homestayInfo = ConversationResponse.HomestayInfo.builder()
        .id(homestay.getId())
        .name(homestay.getName())
        .descriptions(homestay.getDescription())
        .guests(homestay.getGuests())
        .address("Đà Lạt, Lâm Đồng")
        .image(homestay.getImages().get(0).getImageUrl())
        .status("active")
        .nightAmount(800000D)
        .averageRating(4.9)
        .totalReviews(127D)
        .build();

        return ConversationResponse.builder()
            .id(conversation.getId())
            .title(currentUser.getId().equals(conversation.getUser().getId()) ? conversation.getHost().getFullName() : conversation.getUser().getFullName())
            .homestayInfo(homestayInfo)
            .participants(participants)
            .unreadCount(conversation.getUnreadCount())
            .lastMessage(lastMessage)
            .createdAt(conversation.getCreatedAt())
            .lastActivity(conversation.getLastActivity())
            .build();
    }
}
