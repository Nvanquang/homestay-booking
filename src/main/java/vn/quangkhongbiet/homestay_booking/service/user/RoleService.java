package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.RoleResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing roles and permissions.
 * Provides methods for CRUD operations and permission management for roles.
 */
public interface RoleService {
    /**
     * Check if a role exists by id.
     * @param id the role id.
     * @return true if exists, false otherwise.
     */
    boolean isExistById(Long id);

    /**
     * Check if a role exists by name.
     * @param name the role name.
     * @return true if exists, false otherwise.
     */
    boolean isExistByName(String name);

    /**
     * find a role by name.
     * @param name the role name.
     * @return the role entity.
     * @throws EntityNotFoundException if role not found.
     */
    Role findByName(String name);

    /**
     * Create a new role.
     * @param request the CreateRoleRequest dto to create.
     * @return the created role entity.
     * @throws ConflictException if role name already exists.
     */
    Role createRole(CreateRoleRequest request);

    /**
     * Add permissions for a role.
     * @param role the update role DTO.
     * @return the updated role entity.
     * @throws EntityNotFoundException if role or permission not found.
     */
    Role addPermissionForRole(UpdateRoleRequest role);

    /**
     * find role by id.
     * @param id the role id.
     * @return the role DTO.
     * @throws EntityNotFoundException if role not found.
     */
    RoleResponse findById(Long id);

    /**
     * find all roles with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of roles.
     */
    PagedResponse findAll(Specification<Role> spec, Pageable pageable);

    /**
     * Update role partially.
     * @param role the update role DTO.
     * @return the updated role entity.
     * @throws BadRequestAlertException if role not found.
     */
    Role updatePartialRole(UpdateRoleRequest role);

    /**
     * Delete a role by id.
     * @param id the role id.
     * @throws EntityNotFoundException if role not found.
     */
    void deleteById(Long id);

    /**
     * Delete permissions from a role.
     * @param role the update role DTO.
     * @throws EntityNotFoundException if role or permission not found.
     */
    void deletePermissionFromRole(UpdateRoleRequest role);

    /**
     * Convert role entity to role DTO.
     * @param role the role entity.
     * @return the role DTO.
     */
    RoleResponse convertToResRoleDTO(Role role);
}
