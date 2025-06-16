package vn.quangkhongbiet.homestay_booking.service.user.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.repository.PermissionRepository;
import vn.quangkhongbiet.homestay_booking.service.user.PermissionService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final String ENTITY_NAME = "Permission";

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public boolean isExistsById(Long id) {
        return permissionRepository.existsById(id);
    }

    @Override
    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsById(permission.getId());
    }

    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        if (permission == null) {
            throw new BadRequestAlertException("Permission cannot be null", ENTITY_NAME, "nullpermission");
        }
        if(permissionRepository.existsByApiPathAndMethodAndModule(permission.getApiPath() , permission.getMethod(), permission.getModule())) {
            throw new EntityNotFoundException("Permission with the same API path, method, and module already exists", ENTITY_NAME, "duplicatepermission");
        }
        return permissionRepository.save(permission);
    }

    @Override
    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Permission with ID " + id + " not found", ENTITY_NAME, "notfound"));
    }

    @Override
    public PagedResponse getAll(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermissions = this.permissionRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePermissions.getTotalPages());
        meta.setTotal(pagePermissions.getTotalElements());

        result.setMeta(meta);
        result.setResult(pagePermissions.getContent());
        return result;
    }

    @Override
    @Transactional
    public Permission updatePartialPermission(Permission permission) {
        return this.permissionRepository.findById(permission.getId()).map(existingPermission -> {
        if (permission.getName() != null) {
            existingPermission.setName(permission.getName());
        }
        if (permission.getApiPath() != null) {
            existingPermission.setApiPath(permission.getApiPath());
        }
        if (permission.getMethod() != null) {
            existingPermission.setMethod(permission.getMethod());
        }
        if (permission.getModule() != null) {
            existingPermission.setModule(permission.getModule());
        }
        return this.permissionRepository.save(existingPermission);
    }).orElseThrow(() -> new BadRequestAlertException("Permission with ID " + permission.getId() + " not found", ENTITY_NAME, "notfound"));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Permission with ID " + id + " not found", ENTITY_NAME, "notfound");
        }
        // delete permission in role
        Permission currentPermission = this.permissionRepository.findById(id).get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        permissionRepository.deleteById(id);
    }
}