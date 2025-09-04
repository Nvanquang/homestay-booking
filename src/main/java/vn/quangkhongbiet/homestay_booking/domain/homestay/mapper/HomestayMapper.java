package vn.quangkhongbiet.homestay_booking.domain.homestay.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateHomestayRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;

@Mapper(componentModel = "spring")
public interface HomestayMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Homestay createHomestayRequestToHomestay(CreateHomestayRequest request);
} 