package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreatePermissionRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.service.user.PermissionService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Permission", description = "Permission management")
public class PermissionController {
    
    private static final String ENTITY_NAME = "Permission";

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Permission created successfully")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        log.info("REST request to create Permission: {}", request);
        Permission createdPermission = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPermission);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permission successfully")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") Long id) {
        log.info("REST request to get Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(permissionService.findById(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions successfully")
    public ResponseEntity<PagedResponse> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        log.info("REST request to get all Permissions, pageable: {}", pageable);
        PagedResponse result = permissionService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/permissions/{id}")
    @ApiMessage("Permission updated successfully")
    public ResponseEntity<Permission> updatePartialPermission(@PathVariable("id") Long id, @Valid @RequestBody UpdatePermissionRequest permission) {
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
        return ResponseEntity.ok().body(null);
    }
}