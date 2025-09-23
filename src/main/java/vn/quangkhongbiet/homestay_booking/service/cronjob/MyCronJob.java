package vn.quangkhongbiet.homestay_booking.service.cronjob;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.BookingStatus;
import vn.quangkhongbiet.homestay_booking.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class MyCronJob {

    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 11 * * *")
    public void updateStatusBooking() {
        this.bookingRepository.updateStatusBooking(BookingStatus.BOOKED, BookingStatus.COMPLETED);
    }
}
