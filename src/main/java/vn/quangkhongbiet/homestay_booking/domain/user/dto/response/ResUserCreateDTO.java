package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

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
public class ResUserCreateDTO extends ResUserDTO {
    private Instant createdAt;
    private String createdBy;
}
