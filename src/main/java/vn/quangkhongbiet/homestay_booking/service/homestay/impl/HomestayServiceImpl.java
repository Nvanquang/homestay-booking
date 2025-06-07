package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayImageRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    private final HomestayRepository homestayRepository;
    private final HomestayImageRepository homestayImageRepository;
    private final AmenityRepository amenityRepository;

    @Override
    public boolean existsById(long id) {
        return this.homestayRepository.existsById(id);
    }

    @Override
    public Homestay createHomestay(Homestay homestay) {
        // check images and amenities and save
        if (homestay.getImages() != null) {
            homestay.setImages(this.homestayImageRepository
                    .findByIdIn(homestay.getImages().stream().map(HomestayImage::getId).toList()));
        }
        if (homestay.getAmenities() != null) {
            homestay.setAmenities(this.amenityRepository
                    .findByIdIn(homestay.getAmenities().stream().map(Amenity::getId).toList()));
        }
        return homestayRepository.save(homestay);
    }

    @Override
    public Optional<Homestay> findHomestayById(Long id) {
        return homestayRepository.findById(id);
    }

    @Override
    public ResultPaginationDTO findAllHomestays(Specification<Homestay> spec, Pageable pageable) {
        Page<Homestay> pageHomestays = this.homestayRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageHomestays.getTotalPages());
        meta.setTotal(pageHomestays.getTotalElements());

        result.setMeta(meta);
        List<ResHomestayDTO> homestays = pageHomestays.getContent().stream()
                .map(item -> this.convertToResHomestayDTO(item)).toList();
        result.setResult(homestays);
        return result;
    }

    @Override
    public Optional<Homestay> updatePartialHomestay(Homestay updatedHomestay) {
        return this.homestayRepository.findById(updatedHomestay.getId()).map(existingHomestay -> {
            if (updatedHomestay.getName() != null) {
                existingHomestay.setName(updatedHomestay.getName());
            }
            if (updatedHomestay.getDescription() != null) {
                existingHomestay.setDescription(updatedHomestay.getDescription());
            }
            if (updatedHomestay.getStatus() != null) {
                existingHomestay.setStatus(updatedHomestay.getStatus());
            }
            if (updatedHomestay.getMaxGuests() != 0) { // Kiểm tra khác 0 vì maxGuests là kiểu int
                existingHomestay.setMaxGuests(updatedHomestay.getMaxGuests());
            }
            // image

            // Amenity
            return existingHomestay;
        }).map(this.homestayRepository::save);
    }

    @Override
    public void deleteHomestay(Long id) {
        homestayRepository.deleteById(id);
    }

    @Override
    public ResHomestayCreateDTO convertToResCreateHomestayDTO(Homestay homestay) {
        var builder = ResHomestayCreateDTO.builder();
        mapCommonFields(homestay, builder);
        return builder
                .createdAt(homestay.getCreatedAt())
                .createdBy(homestay.getCreatedBy())
                .build();
    }

    @Override
    public ResHomestayUpdatedDTO convertToResUpdatedHomestayDTO(Homestay homestay) {
        var builder = ResHomestayUpdatedDTO.builder();
        mapCommonFields(homestay, builder);
        return builder
                .updatedAt(homestay.getUpdatedAt())
                .updatedBy(homestay.getUpdatedBy())
                .build();
    }

    @Override
    public ResHomestayDTO convertToResHomestayDTO(Homestay homestay) {
        var builder = ResHomestayDTO.builder();
        mapCommonFields(homestay, builder);
        return builder.build();
    }

    private void mapCommonFields(Homestay homestay, ResHomestayDTO.ResHomestayDTOBuilder<?, ?> builder) {
        builder.id(homestay.getId())
                .name(homestay.getName())
                .description(homestay.getDescription())
                .status(homestay.getStatus())
                .address(homestay.getAddress())
                .maxGuests(homestay.getMaxGuests())
                .location(homestay.getLocation());


        // // Map List<Booking> to List<String>
        // List<Long> resBookings = homestay.getBookings() != null
        //         ? homestay.getBookings().stream().map(Booking::getId).toList()
        //         : new ArrayList<>();
        // builder.bookings(resBookings);

        // Map List<HomestayImage> to List<String>
        List<String> resImages = homestay.getImages() != null
                ? homestay.getImages().stream().map(HomestayImage::getImageUrl).toList()
                : new ArrayList<>();
        builder.images(resImages);

        // Map List<Amenity> to List<String>
        List<String> resAmenities = homestay.getAmenities() != null
                ? homestay.getAmenities().stream().map(Amenity::getName).toList()
                : new ArrayList<>();
        builder.amenities(resAmenities);

    }
}
