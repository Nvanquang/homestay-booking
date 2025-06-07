package vn.quangkhongbiet.homestay_booking.domain.booking.entity;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class HomestayAvailabilityId implements Serializable {

    private Long homestayId;

    private LocalDate date;

}
