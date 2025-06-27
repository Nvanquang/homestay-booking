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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "Quản lý người dùng")
public class UserController {
    
    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ApiMessage("Tạo người dùng thành công")
    @Operation(summary = "Tạo user", description = "Tạo mới một user trong hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công"),
        @ApiResponse(responseCode = "409", description = "Dữ liệu đã tồn tại")
    })
    public ResponseEntity<ResUserCreateDTO> createUser(@Valid @RequestBody User user) {
        log.info("REST request to create User: {}", user);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(createdUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Lấy thông tin người dùng thành công")
    @Operation(summary = "Lấy user theo ID", description = "Trả về user theo ID cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy user"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
    })
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) {
        log.info("REST request to get User by id: {}", id);
        if (id <= 0 ){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(userService.findUserById(id)));
    }

    @GetMapping("/users")
    @ApiMessage("Lấy tất cả người dùng thành công")
    @Operation(summary = "Lấy danh sách user", description = "Trả về danh sách user có phân trang, lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<PagedResponse> getAllUsers(Pageable pageable, @Filter Specification<User> spec) {
        log.info("REST request to get all Users, pageable: {}", pageable);
        return ResponseEntity.ok(userService.findAllUsers(spec, pageable));
    }

    @PatchMapping("/users/{id}")
    @ApiMessage("Cập nhật người dùng thành công")
    @Operation(summary = "Cập nhật user", description = "Cập nhật thông tin user theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy user"),
        @ApiResponse(responseCode = "500", description = "Không thể cập nhật user")
    })
    public ResponseEntity<ResUserUpdatedDTO> updatePartialUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO dto) {
        log.info("REST request to update User partially, id: {}, body: {}", id, dto);
        if (id <= 0 ){
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }
        if (!id.equals(dto.getId())) {
            throw new BadRequestAlertException("ID in URL not match content", ENTITY_NAME, "idnull");
        }
        User updatedUser = this.userService.updatePartialUser(dto);
        return ResponseEntity.ok(this.userService.convertToResUpdatedUserDTO(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Xóa người dùng thành công")
    @Operation(summary = "Xóa user", description = "Xóa user theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "400", description = "ID không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy user")
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
