package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.CreateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.UpdateUserResponse;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
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
@RequestMapping("/api/v1")
@Tag(name = "User", description = "User management")
public class UserController {
    
    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ApiMessage("User created successfully")
    @Operation(summary = "Create user", description = "Create a new user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully", content = @Content()),
        @ApiResponse(responseCode = "409", description = "Data already exists", content = @Content())
    })
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody User user) {
        log.info("REST request to create User: {}", user);
        CreateUserResponse createdUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user information successfully")
    @Operation(summary = "Get user by ID", description = "Return user by specific ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content())
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        log.info("REST request to get User by id: {}", id);
        if (id <= 0 ){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok(this.userService.findUserById(id));
    }

    @GetMapping("/users")
    @ApiMessage("Get all users successfully")
    @Operation(summary = "Get user list", description = "Return paginated and filtered user list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<PagedResponse> getAllUsers(Pageable pageable, @Filter Specification<User> spec) {
        log.info("REST request to get all Users, pageable: {}", pageable);
        return ResponseEntity.ok(userService.findAllUsers(spec, pageable));
    }

    @PatchMapping("/users/{id}")
    @ApiMessage("User updated successfully")
    @Operation(summary = "Update user", description = "Update user information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content()),
        @ApiResponse(responseCode = "500", description = "Cannot update user", content = @Content())
    })
    public ResponseEntity<UpdateUserResponse> updatePartialUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest dto) {
        log.info("REST request to update User partially, id: {}, body: {}", id, dto);
        if (id <= 0 ){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idnull");
        }
        UpdateUserResponse updatedUser = this.userService.updatePartialUser(dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("User deleted successfully")
    @Operation(summary = "Delete user", description = "Delete user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content()),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content())
    })
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("REST request to delete User by id: {}", id);
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "invalidid");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
