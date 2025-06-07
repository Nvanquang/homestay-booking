package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface HomestayImageService {
    HomestayImage createHomestayImage(HomestayImage homestayImage);

    Optional<HomestayImage> findHomestayImageById(Long id);

    ResultPaginationDTO findAllHomestayImages(Specification<HomestayImage> spec, Pageable pageable);

    HomestayImage updateHomestayImage(HomestayImage homestayImage);

    void deleteHomestayImage(Long id);
}
