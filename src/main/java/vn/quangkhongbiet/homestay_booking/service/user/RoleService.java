package vn.quangkhongbiet.homestay_booking.service.user;

import java.util.List;
import java.util.Optional;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;

public interface RoleService {
    Role createRole(Role role);

    Optional<Role> findRoleById(Long id);

    List<Role> findAllRoles();

    Role updateRole(Role role);

    void deleteRole(Long id);
}
