package vn.quangkhongbiet.homestay_booking.service.user.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.CreateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UpdateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.domain.user.mapper.UserMapper;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ConflictException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EmailAlreadyUsedException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String ENTITY_NAME = "User";

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    public Boolean existsById(Long id) {
        log.debug("check User exists by id: {}", id);
        return this.userRepository.existsById(id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        log.debug("check User exists by email: {}", email);
        return this.userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        log.debug("create User with user: {}", request);
        
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException();
        }
        if( request.getUserName() != null && userRepository.existsByUserName(request.getUserName())) {
            throw new ConflictException("Username already exists", ENTITY_NAME, "usernameexists");
        }

        User newUser = userMapper.createUserRequestToUser(request);
        if (request.getRoleId() != null) {
            Optional<Role> roleOptional = this.roleRepository.findById(request.getRoleId());
            newUser.setRole(roleOptional.isPresent() ? roleOptional.get() : null);
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setVerified(true);
        return this.convertToResCreateUserDTO(userRepository.save(newUser));
    }

    @Override
    public CreateUserResponse registerUser(RegisterUserRequest request) {
        log.debug("create User with user: {}", request);

        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            User existingUser = userRepository.findByEmail(request.getEmail()).get();
            if(!existingUser.getVerified()) {
                existingUser.setUserName(request.getUserName());
                existingUser.setFullName(request.getFullName());
                existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                existingUser.setGender(request.getGender());
                existingUser.setPhoneNumber(request.getPhoneNumber());
                existingUser.setVerified(false);

                return this.convertToResCreateUserDTO(this.userRepository.save(existingUser));
            }
            else {
                throw new UnauthorizedException("Account already registered", ENTITY_NAME, "emailalreadyused");
            }
        }
        if( request.getUserName() != null && userRepository.existsByUserName(request.getUserName())) {
            throw new ConflictException("Username already exists", ENTITY_NAME, "usernameexists");
        }

        User newUser = userMapper.registerUserRequestToUser(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return this.convertToResCreateUserDTO(userRepository.save(newUser));
    }

    @Override
    public UserResponse findUserById(Long id) {
        log.debug("find User by id: {}", id);
        return this.userRepository.findById(id)
                .map(this::convertToResUserDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id", ENTITY_NAME, "usernotfound"));
    }

    @Override
    public User findUserByEmail(String email) {
        log.debug("find User by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));
    }

    @Override
    public PagedResponse findAllUsers(Specification<User> spec, Pageable pageable) {
        log.debug("find all User with spec: {}, pageable: {}", spec, pageable);
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUsers.getTotalPages());
        meta.setTotal(pageUsers.getTotalElements());

        result.setMeta(meta);
        List<UserResponse> users = pageUsers.getContent().stream().map(item -> this.convertToResUserDTO(item)).toList();
        result.setResult(users);
        return result;
    }

    @Override
    public UpdateUserResponse updatePartialUser(UpdateUserRequest dto) {
        log.debug("update User partially with dto: {}", dto);
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
            if (dto.getAvatar() != null) {
                existingUser.setAvatar(dto.getAvatar());
            }
            if(dto.getVerified() != null){
                existingUser.setVerified(dto.getVerified());
            }
            if(dto.getRole() != null){
                existingUser.setRole(this.roleRepository.findByName(dto.getRole()).get());
            }
            return this.convertToResUpdatedUserDTO(this.userRepository.save(existingUser));
        }).orElseThrow(() -> new EntityNotFoundException("User not found with id", ENTITY_NAME, "usernotfound"));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("delete User by id: {}", id);
        if (!this.userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id", ENTITY_NAME, "usernotfound");
        }
        userRepository.deleteById(id);
    }

    @Override
    public CreateUserResponse convertToResCreateUserDTO(User user) {
        log.debug("convert to ResUserCreateDTO with user: {}", user);
        var builder = CreateUserResponse.builder();
        mapCommonFields(user, builder);
        return builder
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .build();
    }

    @Override
    public UpdateUserResponse convertToResUpdatedUserDTO(User user) {
        log.debug("convert to ResUserUpdatedDTO with user: {}", user);
        var builder = UpdateUserResponse.builder();
        mapCommonFields(user, builder);
        return builder
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    @Override
    public UserResponse convertToResUserDTO(User user) {
        var builder = UserResponse.builder();
        mapCommonFields(user, builder);
        return builder.build();
    }

    private void mapCommonFields(User user, UserResponse.UserResponseBuilder<?, ?> builder) {
        builder.id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isVerified(user.getVerified());
                

        // Map Role to ResRole
        UserResponse.ResRole resRole = user.getRole() != null ? new UserResponse.ResRole(user.getRole().getId(), user.getRole().getName())
                : new UserResponse.ResRole(null, null);
        builder.role(resRole);
    }

    @Override
    public void updateUserToken(String email, String token) {
        log.debug("update User token with email: {}, token: {}", email, token);
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found", ENTITY_NAME, "usernotfound"));
       
        user.setRefreshToken(token);
        this.userRepository.save(user);
    }

    @Override
    public User findUserByRefreshTokenAndEmail(String token, String email) {
        log.debug("find User by refresh token and email: {}, {}", token, email);
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
