package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;

public interface HomestayImageService {
    List<HomestayImage> createHomestayImages(MultipartFile[] files, Long homestayId, String folder);

    List<HomestayImage> findHomestayImageByHomestayId(long id);

    void deleteImage(long id);

}
