package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "posting_date")
    private Instant postingDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "homestay_id")
    private Homestay homestay;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

}