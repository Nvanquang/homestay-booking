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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateRoleDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResRoleDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.service.user.RoleService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Role", description = "Quản lý vai trò người dùng")
public class RoleController {
    
    private static final String ENTITY_NAME = "Role";

    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Tạo mới vai trò thành công")
    @Operation(summary = "Tạo role", description = "Tạo mới một vai trò trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        log.info("REST request to create Role: {}", role);
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRole);
    }

    @PostMapping("/roles/{id}/permissions")
    @ApiMessage("Thêm permissison vào vai trò thành công")
    @Operation(summary = "Thêm permission cho role", description = "Thêm permission vào role theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ")
    })
    public ResponseEntity<Role> addPermissionForRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleDTO role) {
        log.info("REST request to add permission for Role: id: {}, permissions: {}", id, role.getPermissions());
        if (role.getId() <= 0) {
            throw new BadRequestAlertException("Role id cannot be invalid", ENTITY_NAME, "invalidid");
        }
        if(!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        return ResponseEntity.ok(roleService.addPermissionForRole(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Lấy thông tin vai trò thành công")
    @Operation(summary = "Lấy role theo ID", description = "Trả về role theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy role"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy role")
    })
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") Long id) {
        log.info("REST request to get Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(this.roleService.findById(id));
    }

    @GetMapping("/roles")
    @ApiMessage("Lấy danh sách vai trò thành công")
    @Operation(summary = "Lấy danh sách role", description = "Trả về danh sách role có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        log.info("REST request to get all Roles, pageable: {}", pageable);
        PagedResponse result = roleService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage("Cập nhật vai trò thành công")
    @Operation(summary = "Cập nhật role", description = "Cập nhật thông tin role theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy role"),
        @ApiResponse(responseCode = "500", description = "Không thể cập nhật role")
    })
    public ResponseEntity<ResRoleDTO> updatePartialRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleDTO role) {
        log.info("REST request to update Role partially, id: {}, body: {}", id, role);
        if (role.getId() <= 0) {
            throw new BadRequestAlertException("Role invalid", ENTITY_NAME, "invalidrole");
        }
        if(!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        Role updatedRole = roleService.updatePartialRole(role);
        return ResponseEntity.ok().body(this.roleService.convertToResRoleDTO(updatedRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Xóa vai trò thành công")
    @Operation(summary = "Xóa role", description = "Xóa role theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy role")
    })
    public ResponseEntity<Void> deleteRoleById(@PathVariable("id") Long id) {
        log.info("REST request to delete Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/roles/{id}/permissions")
    @ApiMessage("Xóa permission khỏi vai trò thành công")
    @Operation(summary = "Xóa permission khỏi role", description = "Xóa permission khỏi role theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy role")
    })
    public ResponseEntity<Void> deletePermissionFromRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleDTO role) {
        log.info("REST request to delete permission from Role by id: {}, permissions: {}", id, role.getPermissions());
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        if(!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        roleService.deletePermissionFromRole(role);
        return ResponseEntity.noContent().build();
    }
}