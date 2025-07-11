package vn.quangkhongbiet.homestay_booking.domain.homestay.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateAmenityRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Amenity;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "homestays", ignore = true)
    Amenity createAmenityRequestToAmenity(CreateAmenityRequest request);
} 