package vn.quangkhongbiet.homestay_booking.service.user.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateRoleRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.RoleResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.repository.PermissionRepository;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.service.user.RoleService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String ENTITY_NAME = "Role";

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    @Override
    public boolean isExistById(Long id) {
        log.debug("check Role exists by id: {}", id);
        return roleRepository.existsById(id);
    }

    @Override
    public boolean isExistByName(String name) {
        log.debug("check Role exists by name: {}", name);
        return roleRepository.existsByName(name);
    }

    @Override
    public Role findByName(String name) {
        return this.roleRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Role not found", ENTITY_NAME, "rolenotfound"));
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        log.debug("create Role with role: {}", role);
        if(this.roleRepository.existsByName(role.getName())) {
            throw new ConflictException("Role with name " + role.getName() + " already exists", ENTITY_NAME, "nameexists");
        }
        role.setPermissions(
                this.permissionRepository.findByIdIn(role.getPermissions().stream().map(Permission::getId).toList()));
        return this.roleRepository.save(role);
    }

    @Override
    public RoleResponse findById(Long id) {
        log.debug("find Role by id: {}", id);
        return this.roleRepository.findById(id)
                .map(this::convertToResRoleDTO)
                .orElseThrow(() -> new EntityNotFoundException("Role with ID " + id + " not found", ENTITY_NAME,
                        "notfound"));
    }

    @Override
    public PagedResponse findAll(Specification<Role> spec, Pageable pageable) {
        log.debug("find all Role with spec: {}, pageable: {}", spec, pageable);
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRoles.getTotalPages());
        meta.setTotal(pageRoles.getTotalElements());

        result.setMeta(meta);
        List<RoleResponse> resRoles = pageRoles.getContent().stream()
                .map(this::convertToResRoleDTO).toList();
        result.setResult(resRoles);

        return result;
    }

    @Override
    @Transactional
    public Role updatePartialRole(UpdateRoleRequest role) {
        log.debug("update Role partially with role: {}", role);
        return this.roleRepository.findById(role.getId()).map(existingRole -> {
            if (role.getName() != null) {
                existingRole.setName(role.getName());
            }
            if (role.getDescription() != null) {
                existingRole.setDescription(role.getDescription());
            }
            if(role.getActive() != null){
                existingRole.setActive(role.getActive());
            }
            if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
                existingRole.setPermissions(this.permissionRepository
                        .findByIdIn(role.getPermissions()));
            }
            
            return this.roleRepository.save(existingRole);
        }).orElseThrow(() -> new BadRequestAlertException("Role with ID " + role.getId() + " not found", ENTITY_NAME,
                "notfound"));
    }

    @Override
    public Role addPermissionForRole(UpdateRoleRequest role) {
        log.debug("add permission for Role with id = ", role.getId());
        List<Long> permissions = role.getPermissions();
        Role roleDB = this.roleRepository.findById(role.getId()).orElseThrow(() -> new EntityNotFoundException("Role not found", ENTITY_NAME, "rolenotfound"));
        if (permissions != null && !permissions.isEmpty()) {
            for (Long permissionId : permissions) {
                Permission request = this.permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("PermissionId not found", ENTITY_NAME, "permissionidnotfound"));
                if (!roleDB.getPermissions().contains(request)) {
                    roleDB.getPermissions().add(request);
                }
            }
        }
        return this.roleRepository.save(roleDB);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("delete Role by id: {}", id);
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role with ID " + id + " not found", ENTITY_NAME, "notfound");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public void deletePermissionFromRole(UpdateRoleRequest role) {
        log.debug("delete Permission from Role by id: {}", role.getId());
        List<Long> permissions = role.getPermissions();
        Role roleDB = this.roleRepository.findById(role.getId()).orElseThrow(() -> new EntityNotFoundException("Role not found", ENTITY_NAME, "rolenotfound"));
        if (permissions != null && !permissions.isEmpty()) {
            for (Long permissionId : permissions) {
                Permission request = this.permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("PermissionId not found", ENTITY_NAME, "permissionidnotfound"));
                if (roleDB.getPermissions().contains(request)) {
                    roleDB.getPermissions().remove(request);
                }
            }
        }
        this.roleRepository.save(roleDB);
    }

    @Override
    public RoleResponse convertToResRoleDTO(Role role) {
        List<RoleResponse.ResPermission> permissions = role.getPermissions().stream()
                .map(permission -> RoleResponse.ResPermission.builder()
                        .apiPath(permission.getApiPath())
                        .method(permission.getMethod())
                        .module(permission.getModule())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        RoleResponse resRoleDTO = RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .active(role.getActive())
                .permissions(permissions)
                .description(role.getDescription())
                .build();
        return resRoleDTO;
    }

    
}
