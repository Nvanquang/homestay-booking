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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {
    
    private static final String ENTITY_NAME = "Role";

    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Tạo mới vai trò thành công")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        log.info("REST request to create Role: {}", role);
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRole);
    }

    @PostMapping("/roles/{id}/permissions")
    @ApiMessage("Thêm permissison vào vai trò thành công")
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
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") Long id) {
        log.info("REST request to get Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(this.roleService.convertToResRoleDTO(roleService.getById(id)));
    }

    @GetMapping("/roles")
    @ApiMessage("Lấy danh sách vai trò thành công")
    public ResponseEntity<PagedResponse> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        log.info("REST request to get all Roles, pageable: {}", pageable);
        PagedResponse result = roleService.getAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage("Cập nhật vai trò thành công")
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