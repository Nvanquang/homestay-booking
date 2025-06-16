package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.LocationRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayImageService;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    private static final String ENTITY_NAME = "homestay";
    private final HomestayRepository homestayRepository;
    private final HomestayImageService homestayImageService;
    private final AmenityRepository amenityRepository;
    private final LocationRepository locationRepository;

    @Override
    public Boolean existsById(Long id) {
        return this.homestayRepository.existsById(id);
    }

    @Override
    @Transactional
    public Homestay createHomestay(Homestay homestay, MultipartFile[] files, String folder) {

        homestay.setAmenities(this.amenityRepository
                .findByIdIn(homestay.getAmenities().stream().map(Amenity::getId).toList()));

        homestay.setLocation(this.locationRepository.save(homestay.getLocation()));

        Homestay createdHomestay = homestayRepository.save(homestay);

        createdHomestay.setImages(this.homestayImageService.createHomestayImages(files, createdHomestay.getId(), folder));
        
        return homestayRepository.save(createdHomestay);
    }

    @Override
    public Homestay addAmenitiesToHomestay(long homestayId, List<Long> amenityIds) {
        // Kiểm tra homestay tồn tại
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));

        // Kiểm tra hoặc tạo amenity
        for (Long id : amenityIds) {
            Amenity amenity = this.amenityRepository.findById(id).isPresent()
                    ? this.amenityRepository.findById(id).get()
                    : null;
            if (!homestay.getAmenities().contains(amenity) && amenity != null) {
                homestay.getAmenities().add(amenity);
            }
        }
        // save homestay with updated amenities
        return homestayRepository.save(homestay);
    }

    @Override
    public Homestay findHomestayById(Long id) {
        return homestayRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));
    }

    @Override
    public PagedResponse findAllHomestays(Specification<Homestay> spec, Pageable pageable) {
        Page<Homestay> pageHomestays = this.homestayRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

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
    public Homestay updatePartialHomestay(UpdateHomestayDTO updatedHomestay) {
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
            if (updatedHomestay.getMaxGuests() != 0) { // Kiểm tra khác 0 vì maxGuests là kiểu Integer
                existingHomestay.setMaxGuests(updatedHomestay.getMaxGuests());
            }
            return this.homestayRepository.save(existingHomestay);
        }).orElseThrow(() -> new EntityNotFoundException(
                "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));
    }

    @Override
    public void deleteHomestay(Long id) {

        Homestay homestay = this.homestayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));
        // Check if the homestay has images and remove them
        homestay.getImages().forEach(image -> {
            this.homestayImageService.deleteImage(id);
        });

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
