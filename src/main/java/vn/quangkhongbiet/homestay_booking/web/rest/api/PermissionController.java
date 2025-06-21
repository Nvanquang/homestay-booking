package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.service.user.PermissionService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);
    
    private static final String ENTITY_NAME = "Permission";

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Tạo permission thành công")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        log.info("REST request to create Permission: {}", permission);
        Permission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPermission);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Lấy permission thành công")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") Long id) {
        log.info("REST request to get Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(permissionService.getById(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Lấy danh sách permission thành công")
    public ResponseEntity<PagedResponse> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        log.info("REST request to get all Permissions, pageable: {}", pageable);
        PagedResponse result = permissionService.getAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/permissions/{id}")
    @ApiMessage("Cập nhật permission thành công")
    public ResponseEntity<Permission> updatePartialPermission(@PathVariable("id") Long id, @Valid @RequestBody Permission permission) {
        log.info("REST request to update Permission partially, id: {}, body: {}", id, permission);
        if (permission.getId() <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be invalid", ENTITY_NAME, "invalidpermission");
        }
        if(!id.equals(permission.getId())) {
            throw new BadRequestAlertException("Permission ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        Permission updatedPermission = permissionService.updatePartialPermission(permission);
        return ResponseEntity.ok().body(updatedPermission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Xóa permission thành công")
    public ResponseEntity<Map<String, String>> deletePermissionById(@PathVariable("id") Long id) {
        log.info("REST request to delete Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}