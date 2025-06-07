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
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BusinessException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EmailAlreadyUsedException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ErrorConstants;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<ResUserCreateDTO> createUser(@Valid @RequestBody User user) {
        if (user == null) {
            throw new BadRequestAlertException("Dữ liệu người dùng không được null", ENTITY_NAME, "idnull");
        }
        if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyUsedException(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Email " + user.getEmail() + " đã tồn tại", ENTITY_NAME, "emailalreadyexists");
        }
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(createdUser));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        if (id == null) {
            throw new BadRequestAlertException("ID người dùng không được null", ENTITY_NAME, "idnull");
        }
        if (!userService.existsById(id)) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy user với ID " + id, ENTITY_NAME, "usernotfound");
        }
        return ResponseEntity.ok(userService.findUserById(id).get());
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(Pageable pageable, @Filter Specification<User> spec) {
        return ResponseEntity.ok(userService.findAllUsers(spec, pageable));
    }

    @PatchMapping("/users")
    public ResponseEntity<ResUserUpdatedDTO> updatePartialUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            throw new BadRequestAlertException("ID người dùng không được null", ENTITY_NAME, "idnull");
        }
        if (!userService.existsById(user.getId())) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy homestay với ID " + user.getId(), ENTITY_NAME, "usernotfound");

        }
        User updatedUser = this.userService.updatePartialUser(user).get();
        return ResponseEntity.ok(this.userService.convertToResUpdatedUserDTO(updatedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (id == null) {
            throw new BadRequestAlertException("ID người dùng không được null", ENTITY_NAME, "idnull");
        }
        if (!userService.existsById(id)) {
            throw new BusinessException(ErrorConstants.ENTITY_NOT_FOUND_TYPE, "Không tìm thấy homestay với ID " + id, ENTITY_NAME, "homestaynotfound");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
