package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long>, JpaSpecificationExecutor<Amenity> {
    List<Amenity> findByIdIn(List<Long> ids);
}
