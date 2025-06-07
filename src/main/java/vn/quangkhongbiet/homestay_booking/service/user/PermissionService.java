package vn.quangkhongbiet.homestay_booking.service.user;

import java.util.List;
import java.util.Optional;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;

public interface PermissionService {
    Permission createPermission(Permission permission);

    Optional<Permission> findPermissionById(Long id);

    List<Permission> findAllPermissions();

    Permission updatePermission(Permission permission);

    void deletePermission(Long id);
}
