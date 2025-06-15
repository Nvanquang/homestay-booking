package vn.quangkhongbiet.homestay_booking.service.homestay;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface AmenityService {
    Amenity createAmenity(Amenity amenity);

    Amenity findAmenityById(Long id);

    ResultPaginationDTO findAllAmenities(Specification<Amenity> spec, Pageable pageable);

    void deleteAmenity(Long id);
}
