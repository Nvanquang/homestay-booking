package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateRoleDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResRoleDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface RoleService {
    public boolean isExistById(Long id);

    public boolean isExistByName(String name);

    public Role findByName(String name);

    public Role createRole(Role role);

    public Role addPermissionForRole(UpdateRoleDTO role);

    public Role getById(Long id);

    public PagedResponse getAll(Specification<Role> spec, Pageable pageable);

    public Role updatePartialRole(UpdateRoleDTO role);

    public void deleteById(Long id);

    public void deletePermissionFromRole(UpdateRoleDTO role);

    public ResRoleDTO convertToResRoleDTO(Role role);
}
