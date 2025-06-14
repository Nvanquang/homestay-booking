package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private static final String ENTITY_NAME = "Amenity";

    private final AmenityRepository amenityRepository;

    @Override
    public Amenity createAmenity(Amenity amenity) {
        if (amenityRepository.existsByName(amenity.getName())) {
            throw new BadRequestAlertException(
                "Tiện nghi với name = : " + amenity.getName() + " already exists",
                ENTITY_NAME,
                "amenityexists"
            );
        }

        return amenityRepository.save(amenity);
    }

    @Override
    public Optional<Amenity> findAmenityById(Long id) {
        return amenityRepository.findById(id);
    }

    @Override
    public ResultPaginationDTO findAllAmenities(Specification<Amenity> spec, Pageable pageable) {
        Page<Amenity> pageAmenities = this.amenityRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAmenities.getTotalPages());
        meta.setTotal(pageAmenities.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageAmenities.getContent());
        return result;
    }

    @Override
    public void deleteAmenity(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new BadRequestAlertException(
                "Tiện nghi với ID : " + id + " không tồn tại!",
                ENTITY_NAME,
                "idnotfound"
            );
        }
        // Check if the amenity is used in any homestay and remove it from those homestays
        Optional<Amenity> amenity = amenityRepository.findById(id);
        Amenity currentAmenity = amenity.get();
        currentAmenity.getHomestays().forEach(homestay -> homestay.getAmenities().remove(currentAmenity));

        amenityRepository.deleteById(id);
    }
}