package vn.quangkhongbiet.homestay_booking.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Role createRoleRequestToRole(CreateRoleRequest request);
} 