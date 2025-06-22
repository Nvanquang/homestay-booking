package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface PermissionService {
    public boolean isExistsById(Long id);

    public boolean isPermissionExist(Permission permission);

    public Permission createPermission(Permission permission);

    public Permission getById(Long id);

    public PagedResponse getAll(Specification<Permission> spec, Pageable pageable);

    public Permission updatePartialPermission(UpdatePermissionDTO permission);

    public void deleteById(Long id);
}
