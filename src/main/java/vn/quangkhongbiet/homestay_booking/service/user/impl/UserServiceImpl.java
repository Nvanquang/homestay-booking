package vn.quangkhongbiet.homestay_booking.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResRole;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EmailAlreadyUsedException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String ENTITY_NAME = "User";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Boolean existsById(Long id) {
        return this.userRepository.existsById(id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public User createUser(User user) {

        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyUsedException(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Email already exists", ENTITY_NAME,
                    "emailalreadyexists");
        }
        // check role
        if (user.getRole() != null) {
            Optional<Role> roleOptional = this.roleRepository.findById(user.getRole().getId());
            user.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestAlertException("User not found with id", ENTITY_NAME, "usernotfound"));
    }

    @Override
    public ResultPaginationDTO findAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUsers.getTotalPages());
        meta.setTotal(pageUsers.getTotalElements());

        result.setMeta(meta);
        List<ResUserDTO> users = pageUsers.getContent().stream().map(item -> this.convertToResUserDTO(item)).toList();
        result.setResult(users);
        return result;
    }

    @Override
    public User updatePartialUser(UpdateUserDTO dto) {
        return this.userRepository.findById(dto.getId()).map(existingUser -> {
            if (dto.getUserName() != null) {
                existingUser.setUserName(dto.getUserName());
            }
            if (dto.getGender() != null) {
                existingUser.setGender(dto.getGender());
            }
            if (dto.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(dto.getPhoneNumber());
            }
            if (dto.getFullName() != null) {
                existingUser.setFullName(dto.getFullName());
            }
            // role
            return this.userRepository.save(existingUser);
        }).orElseThrow(() -> new BadRequestAlertException("User not found with id", ENTITY_NAME, "usernotfound"));
    }

    @Override
    public void deleteUser(Long id) {
        if (!this.userRepository.existsById(id)) {
            throw new BadRequestAlertException("User not found with id", ENTITY_NAME, "usernotfound");
        }
        userRepository.deleteById(id);
    }

    @Override
    public ResUserCreateDTO convertToResCreateUserDTO(User user) {
        var builder = ResUserCreateDTO.builder();
        mapCommonFields(user, builder);
        return builder
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .build();
    }

    @Override
    public ResUserUpdatedDTO convertToResUpdatedUserDTO(User user) {
        var builder = ResUserUpdatedDTO.builder();
        mapCommonFields(user, builder);
        return builder
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
        var builder = ResUserDTO.builder();
        mapCommonFields(user, builder);
        return builder.build();
    }

    private void mapCommonFields(User user, ResUserDTO.ResUserDTOBuilder<?, ?> builder) {
        builder.id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .gender(user.getGender());

        // Map Role to ResRole
        ResRole resRole = user.getRole() != null ? new ResRole(user.getRole().getId(), user.getRole().getName())
                : new ResRole(null, null);
        builder.role(resRole);
    }
}
