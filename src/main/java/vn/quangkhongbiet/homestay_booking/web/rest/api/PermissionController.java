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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdatePermissionDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.service.user.PermissionService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Permission", description = "Quản lý quyền người dùng")
public class PermissionController {
    
    private static final String ENTITY_NAME = "Permission";

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Tạo permission thành công")
    @Operation(summary = "Tạo permission", description = "Tạo mới một permission trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        log.info("REST request to create Permission: {}", permission);
        Permission createdPermission = permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPermission);
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Lấy permission thành công")
    @Operation(summary = "Lấy permission theo ID", description = "Trả về permission theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy permission"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy permission")
    })
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") Long id) {
        log.info("REST request to get Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(permissionService.getById(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Lấy danh sách permission thành công")
    @Operation(summary = "Lấy danh sách permission", description = "Trả về danh sách permission có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        log.info("REST request to get all Permissions, pageable: {}", pageable);
        PagedResponse result = permissionService.getAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/permissions/{id}")
    @ApiMessage("Cập nhật permission thành công")
    @Operation(summary = "Cập nhật permission", description = "Cập nhật thông tin permission theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy permission"),
         @ApiResponse(responseCode = "500", description = "Không thể cập nhật permission")
    })
    public ResponseEntity<Permission> updatePartialPermission(@PathVariable("id") Long id, @Valid @RequestBody UpdatePermissionDTO permission) {
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
    @Operation(summary = "Xóa permission", description = "Xóa permission theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy permission")
    })
    public ResponseEntity<Map<String, String>> deletePermissionById(@PathVariable("id") Long id) {
        log.info("REST request to delete Permission by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Permission ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}