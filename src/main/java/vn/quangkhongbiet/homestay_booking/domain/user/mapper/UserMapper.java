package vn.quangkhongbiet.homestay_booking.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User registerUserRequestToUser(RegisterUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User createUserRequestToUser(CreateUserRequest request);
}