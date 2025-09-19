package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Integer rating;

    private String comment;

    private Instant postingDate;

    private String hostReply;

    private ReviewerInfo user;

    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    public static class ReviewerInfo {
        private Long id;
        private String name;
        private String avatar;
    }

}
