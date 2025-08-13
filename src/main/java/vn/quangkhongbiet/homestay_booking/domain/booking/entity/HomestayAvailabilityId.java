package vn.quangkhongbiet.homestay_booking.domain.booking.entity;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomestayAvailabilityId implements Serializable {

    private Long homestayId;

    private LocalDate date;

}
