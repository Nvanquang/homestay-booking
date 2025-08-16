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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.RoleResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.service.user.RoleService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Role", description = "Role management")
public class RoleController {
    
    private static final String ENTITY_NAME = "Role";

    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Role created successfully")
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleRequest request) {
        log.info("REST request to create Role: {}", request);
        Role createdRole = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRole);
    }

    @PostMapping("/roles/{id}/permissions")
    @ApiMessage("Permission added to role successfully")
    public ResponseEntity<Role> addPermissionForRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest role) {
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
    @ApiMessage("Get role information successfully")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable("id") Long id) {
        log.info("REST request to get Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(this.roleService.findById(id));
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles successfully")
    public ResponseEntity<PagedResponse> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        log.info("REST request to get all Roles, pageable: {}", pageable);
        PagedResponse result = roleService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage("Role updated successfully")
    public ResponseEntity<RoleResponse> updatePartialRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest role) {
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
    @ApiMessage("Role deleted successfully")
    public ResponseEntity<Void> deleteRoleById(@PathVariable("id") Long id) {
        log.info("REST request to delete Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        roleService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/roles/{id}/permissions")
    @ApiMessage("Permission removed from role successfully")
    public ResponseEntity<Void> deletePermissionFromRole(@PathVariable("id") Long id, @Valid @RequestBody UpdateRoleRequest role) {
        log.info("REST request to delete permission from Role by id: {}, permissions: {}", id, role.getPermissions());
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        if(!id.equals(role.getId())) {
            throw new BadRequestAlertException("Role ID in path and body must match", ENTITY_NAME, "idnotmatch");
        }
        roleService.deletePermissionFromRole(role);
        return ResponseEntity.ok().body(null);
    }
}