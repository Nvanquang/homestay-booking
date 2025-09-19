package vn.quangkhongbiet.homestay_booking.domain.homestay.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.CreateReviewRequest;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postingDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "homestay", ignore = true)
    @Mapping(target = "booking", ignore = true)
    Review createReviewRequestToReview(CreateReviewRequest request);
} 