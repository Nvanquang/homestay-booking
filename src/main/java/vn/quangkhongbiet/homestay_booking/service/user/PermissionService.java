package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing permissions.
 * Provides methods for CRUD operations and queries on permissions.
 */
public interface PermissionService {
    /**
     * Check if a permission exists by id.
     * @param id the permission id.
     * @return true if exists, false otherwise.
     */
    boolean isExistsById(Long id);

    /**
     * Check if a permission exists by entity.
     * @param permission the permission entity.
     * @return true if exists, false otherwise.
     */
    boolean isPermissionExist(Permission permission);

    /**
     * Create a new permission.
     * @param permission the permission entity to create.
     * @return the created permission entity.
     * @throws vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException if permission with same API path, method, and module already exists.
     */
    Permission createPermission(Permission permission);

    /**
     * find a permission by id.
     * @param id the permission id.
     * @return the permission entity.
     * @throws vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException if permission not found.
     */
    Permission findById(Long id);

    /**
     * find all permissions with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of permissions.
     */
    PagedResponse findAll(Specification<Permission> spec, Pageable pageable);

    /**
     * Update permission partially.
     * @param permission the update permission DTO.
     * @return the updated permission entity.
     * @throws vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException if permission not found.
     */
    Permission updatePartialPermission(UpdatePermissionDTO permission);

    /**
     * Delete a permission by id.
     * @param id the permission id.
     * @throws vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException if permission not found.
     */
    void deleteById(Long id);
}
