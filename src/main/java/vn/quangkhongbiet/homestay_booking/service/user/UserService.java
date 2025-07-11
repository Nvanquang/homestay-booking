package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.CreateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.CreateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UpdateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing users.
 * Provides methods for user CRUD operations, token management, and user queries.
 */
public interface UserService {
    /**
     * Check if a user exists by id.
     * @param id the user id.
     * @return true if exists, false otherwise.
     */
    Boolean existsById(Long id);

    /**
     * Check if a user exists by email.
     * @param email the user email.
     * @return true if exists, false otherwise.
     */
    Boolean existsByEmail(String email);
    
    /**
     * Create a new user.
     * @param request the CreateUserRequest dto to create.
     * @return the created CreateUserResponse DTO.
     * @throws EmailAlreadyUsedException if email already exists.
     */
    CreateUserResponse createUser(CreateUserRequest request);

    /**
     * Register user.
     * @param request the user entity to create.
     * @return the RegisterUserRequest user DTO.
     * @throws EmailAlreadyUsedException if email already exists.
     */
    CreateUserResponse registerUser(RegisterUserRequest request);

    /**
     * find a user by id.
     * @param id the user id.
     * @return the user DTO.
     * @throws EntityNotFoundException if user not found.
     */
    UserResponse findUserById(Long id);

    /**
     * find a user by email.
     * @param email the user email.
     * @return the user entity.
     * @throws EntityNotFoundException if user not found.
     */
    User findUserByEmail(String email);

    /**
     * find all users with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of users.
     */
    PagedResponse findAllUsers(Specification<User> spec, Pageable pageable);

    /**
     * Update user partially.
     * @param dto the update DTO.
     * @return the updated user DTO.
     * @throws EntityNotFoundException if user not found.
     */
    UpdateUserResponse updatePartialUser(UpdateUserRequest dto);

    /**
     * Delete a user by id.
     * @param id the user id.
     * @throws EntityNotFoundException if user not found.
     */
    void deleteUser(Long id);

    /**
     * Convert user entity to create user DTO.
     * @param user the user entity.
     * @return the create user DTO.
     */
    CreateUserResponse convertToResCreateUserDTO(User user);

    /**
     * Convert user entity to updated user DTO.
     * @param user the user entity.
     * @return the updated user DTO.
     */
    UpdateUserResponse convertToResUpdatedUserDTO(User user);

    /**
     * Convert user entity to user DTO.
     * @param user the user entity.
     * @return the user DTO.
     */
    UserResponse convertToResUserDTO(User user);

    /**
     * Update user's refresh token by email.
     * @param email the user email.
     * @param token the new refresh token.
     * @throws EntityNotFoundException if user not found.
     */
    void updateUserToken(String email, String token);

    /**
     * Find user by refresh token and email.
     * @param token the refresh token.
     * @param email the user email.
     * @return the user entity, or null if not found.
     */
    User findUserByRefreshTokenAndEmail(String token, String email);
}
