package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService{

    private final AmenityRepository amenityRepository;
    @Override
    public Amenity createAmenity(Amenity amenity) {
        return this.amenityRepository.save(amenity);
    }

    @Override
    public Optional<Amenity> findAmenityById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAmenityById'");
    }

    @Override
    public ResultPaginationDTO findAllAmenities(Specification<Amenity> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllAmenities'");
    }

    @Override
    public Optional<Amenity> updatepartialAmenity(Amenity amenity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatepartialAmenity'");
    }

    @Override
    public void deleteAmenity(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAmenity'");
    }
    
}
