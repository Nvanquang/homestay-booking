package vn.quangkhongbiet.homestay_booking.service.user.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.repository.PermissionRepository;
import vn.quangkhongbiet.homestay_booking.service.user.PermissionService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private static final String ENTITY_NAME = "Permission";

    private final PermissionRepository permissionRepository;

    @Override
    public boolean isExistsById(Long id) {
        log.debug("check Permission exists by id: {}", id);
        return permissionRepository.existsById(id);
    }

    @Override
    public boolean isPermissionExist(Permission permission) {
        log.debug("check Permission exists by permission: {}", permission);
        return permissionRepository.existsById(permission.getId());
    }

    @Override
    public Permission createPermission(Permission permission) {
        log.debug("create Permission with permission: {}", permission);
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
        log.debug("find Permission by id: {}", id);
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("Permission with ID " + id + " not found", ENTITY_NAME, "notfound"));
    }

    @Override
    public PagedResponse getAll(Specification<Permission> spec, Pageable pageable) {
        log.debug("find all Permission with spec: {}, pageable: {}", spec, pageable);
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
    public Permission updatePartialPermission(UpdatePermissionDTO permission) {
        log.debug("update Permission partially with permission: {}", permission);
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
        log.debug("delete Permission by id: {}", id);
        if (!permissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Permission with ID " + id + " not found", ENTITY_NAME, "notfound");
        }
        permissionRepository.deleteById(id);
    }
}