package vn.quangkhongbiet.homestay_booking.domain.chat.dto.response;

import java.time.Instant;
import java.util.List;

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
public class ConversationResponse {
    private Long id;
    private String title;
    private List<UserInfo> participants;
    private Integer unreadCount;
    private MessageRes lastMessage;
    private Instant lastActivity;
    private HomestayInfo homestayInfo;
    private Instant createdAt;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String fullName;
        private String avatar;
        private String role;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageRes {
        private Long id;
        private String content;
        private Long senderId;
        private MessageType type;
        private Instant timestamp;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomestayInfo {
        private Long id;
        private String name;
        private String descriptions;
        private Integer guests;
        private String address;
        private String image;
        private String status;
        private Double nightAmount;
        private Double averageRating;
        private Double totalReviews;
    }
}
