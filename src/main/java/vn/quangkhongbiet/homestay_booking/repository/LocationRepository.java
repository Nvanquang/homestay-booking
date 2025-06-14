package vn.quangkhongbiet.homestay_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByWardAndDistrictAndCity(String ward, String district, String city);

    Location findByWardAndDistrictAndCity(String ward, String district, String city);
}
