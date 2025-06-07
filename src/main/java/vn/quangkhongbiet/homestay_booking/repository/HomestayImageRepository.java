package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;

@Repository
public interface HomestayImageRepository extends JpaRepository<HomestayImage, Long>, JpaSpecificationExecutor<HomestayImage> {
    List<HomestayImage> findByIdIn(List<Long> ids);
}
