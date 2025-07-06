package vn.quangkhongbiet.homestay_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync 
@SpringBootApplication
public class HomestayBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomestayBookingApplication.class, args);
	}

}
