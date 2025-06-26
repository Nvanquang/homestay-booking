package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.AmenityService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    
    private static final String ENTITY_NAME = "Amenity";

    private final AmenityRepository amenityRepository;

    @Override
    public Amenity createAmenity(Amenity amenity) {
        log.debug("create Amenity with amenity: {}", amenity);
        if (amenityRepository.existsByName(amenity.getName())) {
            throw new BadRequestAlertException(
                    "Amenity already exists",
                    ENTITY_NAME,
                    "amenityexists");
        }

        return amenityRepository.save(amenity);
    }

    @Override
    public Amenity findAmenityById(Long id) {
        log.debug("find Amenity by id: {}", id);
        return amenityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Amenity not found with id!",
                ENTITY_NAME,
                "idnotfound"));
    }

    @Override
    public PagedResponse findAllAmenities(Specification<Amenity> spec, Pageable pageable) {
        log.debug("find all Amenity with spec: {}, pageable: {}", spec, pageable);
        Page<Amenity> pageAmenities = this.amenityRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAmenities.getTotalPages());
        meta.setTotal(pageAmenities.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageAmenities.getContent());
        return result;
    }

    @Override
    @Transactional
    public void deleteAmenity(Long id) {
        log.debug("delete Amenity by id: {}", id);
        if (!amenityRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Amenity not found with id!",
                    ENTITY_NAME,
                    "idnotfound");
        }
        amenityRepository.deleteById(id);
    }
}