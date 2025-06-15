package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface RoleService {
    public boolean isExistById(Long id);

    public boolean isExistByName(String name);

    public Role createRole(Role role);

    public Role getById(Long id);

    public ResultPaginationDTO getAll(Specification<Role> spec, Pageable pageable);

    public Role updatePartialRole(Role role);

    public void deleteById(Long id);
}
