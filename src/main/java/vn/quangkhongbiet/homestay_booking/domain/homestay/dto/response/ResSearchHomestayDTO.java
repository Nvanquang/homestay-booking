package vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response;

import java.util.List;

import vn.quangkhongbiet.homestay_booking.domain.homestay.constant.HomestayStatus;

public interface ResSearchHomestayDTO {
    Long getId();

    String getName();

    String getDescription();

    Integer getGuests();

    HomestayStatus getStatus();

    String getPhoneNumber();

    Double getNightAmount();

    Double getTotalAmount();

    String getAddress();

    Double getLongitude();

    Double getLatitude();

    List<String> getImages(); 
    
    List<Long> getAmenities();
}
