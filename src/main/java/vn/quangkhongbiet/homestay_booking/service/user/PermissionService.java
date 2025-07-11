package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreatePermissionRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionRequest;
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
     * @param request the CreatePermissionRequest dto to create.
     * @return the created permission entity.
     * @throws ConflictException if permission with same API path, method, and module already exists.
     */
    Permission createPermission(CreatePermissionRequest request);

    /**
     * find a permission by id.
     * @param id the permission id.
     * @return the permission entity.
     * @throws EntityNotFoundException if permission not found.
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
     * @throws EntityNotFoundException if permission not found.
     */
    Permission updatePartialPermission(UpdatePermissionRequest permission);

    /**
     * Delete a permission by id.
     * @param id the permission id.
     * @throws EntityNotFoundException if permission not found.
     */
    void deleteById(Long id);
}
