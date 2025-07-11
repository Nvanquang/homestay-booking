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
@AllArgsConstructor
@NoArgsConstructor
public class CreateHomestayResponse extends HomestayResponse{
    private Instant createdAt;
    private String createdBy;
}
