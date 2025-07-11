package vn.quangkhongbiet.homestay_booking.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreatePermissionRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Permission createPermissionRequestToPermission(CreatePermissionRequest request);
} 