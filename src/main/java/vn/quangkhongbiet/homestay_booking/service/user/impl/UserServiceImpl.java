package vn.quangkhongbiet.homestay_booking.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResRole;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Boolean existsById(Long id){
        return this.userRepository.existsById(id);
    }

    @Override
    public Boolean existsByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public User createUser(User user) {
        // check role
         if (user.getRole() != null) {
            Optional<Role> roleOptional = this.roleRepository.findById(user.getRole().getId());
            user.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
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
    public Optional<User> updatePartialUser(User updatedUser) {
        return this.userRepository.findById(updatedUser.getId()).map(existingUser -> {
			if (updatedUser.getUserName() != null) {
				existingUser.setUserName(updatedUser.getUserName());
			}
			if (updatedUser.getGender() != null) {
				existingUser.setGender(updatedUser.getGender());
			}
            if (updatedUser.getPhoneNumber() != null) {
				existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
			}
            if (updatedUser.getFullName() != null) {
				existingUser.setFullName(updatedUser.getFullName());
			}
            // role
			return existingUser;
		}).map(this.userRepository::save);
    }

    @Override
    public void deleteUser(Long id) {
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
        ResRole resRole = user.getRole() != null ? new ResRole(user.getRole().getId(), user.getRole().getName()) : new ResRole(null, null);
        builder.role(resRole);
    }
}
