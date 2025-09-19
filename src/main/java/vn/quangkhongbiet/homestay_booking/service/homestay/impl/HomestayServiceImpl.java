package vn.quangkhongbiet.homestay_booking.service.homestay.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.booking.constant.AvailabilityStatus;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.SearchHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.CreateHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.HomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.UpdateHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.SearchHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.HomestayImage;
import vn.quangkhongbiet.homestay_booking.domain.homestay.mapper.HomestayMapper;
import vn.quangkhongbiet.homestay_booking.repository.AmenityRepository;
import vn.quangkhongbiet.homestay_booking.repository.HomestayRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.homestay.UploadFileService;
import vn.quangkhongbiet.homestay_booking.service.homestay.HomestayService;
import vn.quangkhongbiet.homestay_booking.utils.DateUtil;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    private static final String ENTITY_NAME = "homestay";

    private final HomestayRepository homestayRepository;

    private final UploadFileService homestayImageService;

    private final AmenityRepository amenityRepository;

    private final HomestayMapper homestayMapper;

    private final UserRepository userRepository;

    @Override
    public Boolean existsById(Long id) {
        log.debug("check Homestay exists by id: {}", id);
        return this.homestayRepository.existsById(id);
    }

    @Override
    @Transactional
    public CreateHomestayResponse createHomestay(CreateHomestayRequest request, MultipartFile[] files, String folder) {
        log.debug("create Homestay with homestay: {}, folder: {}, files: {}", request, folder,
                files != null ? files.length : 0);

        Homestay newHomestay = this.homestayMapper.createHomestayRequestToHomestay(request);
        newHomestay.setAmenities(this.amenityRepository.findByIdIn(request.getAmenities()));
        newHomestay.setHost(this.userRepository.findById(request.getHostId()).orElseThrow(() -> new EntityNotFoundException(
                "Host not found with id", "Homestay", "hostnotfound")));

        Homestay createdHomestay = homestayRepository.save(newHomestay);

        this.homestayImageService.createHomestayImages(files, createdHomestay.getId(), folder);
        homestayRepository.updateGeom(createdHomestay.getId(), createdHomestay.getLongitude(),
                createdHomestay.getLatitude());

        return this.convertToResCreateHomestayDTO(this.homestayRepository.save(createdHomestay));
    }

    @Override
    public UpdateHomestayResponse addAmenitiesToHomestay(long homestayId, List<Long> amenityIds) {
        log.debug("add amenities to Homestay, homestayId: {}, amenityIds: {}", homestayId, amenityIds);

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

        return this.convertToResUpdatedHomestayDTO(homestayRepository.save(homestay));
    }

    @Override
    public HomestayResponse findHomestayById(Long id) {
        log.debug("find Homestay by id: {}", id);
        if (!this.homestayRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Homestay not found with id", ENTITY_NAME, "homestaynotfound");
        }
        return this.convertToResHomestayDTO(this.homestayRepository.findById(id).get());
    }

    @Override
    public List<SearchHomestayResponse> searchHomestays(SearchHomestayRequest request) {
        log.debug("search Homestay with request: {}", request);
        request.setStatus(AvailabilityStatus.AVAILABLE);

        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = request.getCheckoutDate();
        final var currentDate = LocalDate.now();

        if (checkinDate.isBefore(currentDate) || checkinDate.isAfter(checkoutDate)) {
            throw new BadRequestAlertException("Checkin-date invalid", ENTITY_NAME, "checkindateinvalid");
        }

        int nights = (int) DateUtil.getDiffInDays(checkinDate, checkoutDate);
        checkoutDate = checkoutDate.minusDays(1);

        return homestayRepository.searchHomestay(
                request.getLongitude(),
                request.getLatitude(),
                request.getRadius(),
                checkinDate,
                checkoutDate,
                nights,
                request.getGuests(),
                request.getStatus().toString());
    }

    @Override
    public PagedResponse findAllHomestays(Specification<Homestay> spec, Pageable pageable) {
        log.debug("find all Homestay with spec: {}, pageable: {}", spec, pageable);
        Page<Homestay> pageHomestays = this.homestayRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageHomestays.getTotalPages());
        meta.setTotal(pageHomestays.getTotalElements());

        result.setMeta(meta);
        List<HomestayResponse> homestays = pageHomestays.getContent().stream()
                .map(item -> this.convertToResHomestayDTO(item)).toList();
        result.setResult(homestays);
        return result;
    }

    @Override
    @Transactional
    public UpdateHomestayResponse updatePartialHomestay(UpdateHomestayRequest updatedHomestay, MultipartFile[] files,
            String folder) {
        log.debug("update Homestay partially with dto: {}", updatedHomestay);
        return this.homestayRepository.findById(updatedHomestay.getId()).map(existingHomestay -> {
            Homestay homestayDB = this.homestayRepository.findById(updatedHomestay.getId()).get();
            if (updatedHomestay.getName() != null) {
                existingHomestay.setName(updatedHomestay.getName());
            }
            if (updatedHomestay.getDescription() != null) {
                existingHomestay.setDescription(updatedHomestay.getDescription());
            }
            if (updatedHomestay.getStatus() != null) {
                existingHomestay.setStatus(updatedHomestay.getStatus());
            }
            if (updatedHomestay.getGuests() != 0 || updatedHomestay.getGuests() != null) { // Kiểm tra khác 0 vì guests là kiểu Integer
                existingHomestay.setGuests(updatedHomestay.getGuests());
            }
            if (updatedHomestay.getHostId() != null) {
                existingHomestay.setHost(this.userRepository.findById(updatedHomestay.getHostId()).orElseThrow(() -> new EntityNotFoundException(
                        "Host not found with id", "Homestay", "hostnotfound")));
            }
            if (updatedHomestay.getAmenities() != null) {
                List<Amenity> currentAmenities = new ArrayList<>(homestayDB.getAmenities());
                for (Amenity amenity : currentAmenities) {
                    amenity.getHomestays().remove(homestayDB);
                }
                homestayDB.getAmenities().clear();

                // Thêm amenities mới
                List<Amenity> newAmenities = updatedHomestay.getAmenities().stream()
                        .map(id -> amenityRepository.findById(id)
                                .orElseThrow(() -> new BadRequestAlertException("Invalid amenity ID", "Homestay",
                                        "amenityinvalid")))
                        .collect(Collectors.toList());
                homestayDB.setAmenities(newAmenities);
                newAmenities.forEach(amenity -> amenity.getHomestays().add(homestayDB));
            }
            if(updatedHomestay.getDeletedImages() != null && !updatedHomestay.getDeletedImages().isEmpty()) {
                for (String url : updatedHomestay.getDeletedImages()) {
                    homestayImageService.deleteByImageUrl(url);
                }
            }
            if (files != null && files.length > 0) {
                // save image
                this.homestayImageService.createHomestayImages(files, updatedHomestay.getId(), folder);
            }
            return this.convertToResUpdatedHomestayDTO(this.homestayRepository.save(existingHomestay));
        }).orElseThrow(() -> new EntityNotFoundException(
                "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));
    }

    @Override
    @Transactional
    public void deleteHomestay(Long id) {
        log.debug("delete Homestay by id: {}", id);
        Homestay homestay = this.homestayRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Homestay not found with id", ENTITY_NAME, "homestaynotfound"));
        // Check if the homestay has images and remove them
        List<HomestayImage> images = new ArrayList<>(homestay.getImages());
        images.forEach(image -> {
            this.homestayImageService.deleteImage(image.getId());
        });

        homestayRepository.deleteById(id);
    }

    @Override
    public CreateHomestayResponse convertToResCreateHomestayDTO(Homestay homestay) {
        var builder = CreateHomestayResponse.builder();
        mapCommonFields(homestay, builder);
        return builder
                .createdAt(homestay.getCreatedAt())
                .createdBy(homestay.getCreatedBy())
                .build();
    }

    @Override
    public UpdateHomestayResponse convertToResUpdatedHomestayDTO(Homestay homestay) {
        var builder = UpdateHomestayResponse.builder();
        mapCommonFields(homestay, builder);
        return builder
                .updatedAt(homestay.getUpdatedAt())
                .updatedBy(homestay.getUpdatedBy())
                .build();
    }

    @Override
    public HomestayResponse convertToResHomestayDTO(Homestay homestay) {
        var builder = HomestayResponse.builder();
        mapCommonFields(homestay, builder);
        return builder.build();
    }

    private void mapCommonFields(Homestay homestay, HomestayResponse.HomestayResponseBuilder<?, ?> builder) {
        builder.id(homestay.getId())
                .name(homestay.getName())
                .description(homestay.getDescription())
                .status(homestay.getStatus())
                .address(homestay.getAddress())
                .phoneNumber(homestay.getPhoneNumber())
                .guests(homestay.getGuests())
                .longitude(homestay.getLongitude())
                .latitude(homestay.getLatitude())
                .totalReviews(homestay.getReviews() != null ? homestay.getReviews().size() : 0)
                .averageRating(homestay.getReviews() != null ? homestay.getReviews().stream()
                        .mapToInt(r -> r.getRating())
                        .average()
                        .orElse(0.0) : 0.0);

        
        List<String> resImages = homestay.getImages() != null
                ? homestay.getImages().stream().map(HomestayImage::getImageUrl).toList()
                : new ArrayList<>();
        builder.images(resImages);

        builder.amenities(homestay.getAmenities());

        HomestayResponse.HostInfo hostInfo = HomestayResponse.HostInfo.builder()
                .id(homestay.getHost() != null ? homestay.getHost().getId() : null)
                .name(homestay.getHost() != null ? homestay.getHost().getFullName() : null)
                .avatar(homestay.getHost() != null ? homestay.getHost().getAvatar() : null)
                .build();
        builder.host(hostInfo);

    }

}
