package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public class HomestayImageServiceImpl implements HomestayImageService{

    @Override
    public HomestayImage createHomestayImage(HomestayImage homestayImage) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createHomestayImage'");
    }

    @Override
    public Optional<HomestayImage> findHomestayImageById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findHomestayImageById'");
    }

    @Override
    public ResultPaginationDTO findAllHomestayImages(Specification<HomestayImage> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllHomestayImages'");
    }

    @Override
    public HomestayImage updateHomestayImage(HomestayImage homestayImage) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateHomestayImage'");
    }

    @Override
    public void deleteHomestayImage(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteHomestayImage'");
    }


    
}
