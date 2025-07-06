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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create role", description = "Create a new role in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully", content = @Content()),
        @ApiResponse(responseCode = "409", description = "Data already exists", content = @Content())
    })
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        log.info("REST request to create Role: {}", role);
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRole);
    }

    @PostMapping("/roles/{id}/permissions")
    @ApiMessage("Permission added to role successfully")
    @Operation(summary = "Add permission to role", description = "Add permission to role by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content())
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
    @ApiMessage("Get role information successfully")
    @Operation(summary = "Get role by ID", description = "Return role by specific ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content())
    })
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") Long id) {
        log.info("REST request to get Role by id: {}", id);
        if (id == null || id <= 0) {
            throw new BadRequestAlertException("Role ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        return ResponseEntity.ok().body(this.roleService.findById(id));
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles successfully")
    @Operation(summary = "Get role list", description = "Return paginated and filtered role list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<PagedResponse> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        log.info("REST request to get all Roles, pageable: {}", pageable);
        PagedResponse result = roleService.findAll(spec, pageable);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage("Role updated successfully")
    @Operation(summary = "Update role", description = "Update role information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content()),
        @ApiResponse(responseCode = "500", description = "Cannot update role", content = @Content())
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
    @ApiMessage("Role deleted successfully")
    @Operation(summary = "Delete role", description = "Delete role by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content())
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
    @ApiMessage("Permission removed from role successfully")
    @Operation(summary = "Remove permission from role", description = "Remove permission from role by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Role not found", content = @Content())
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