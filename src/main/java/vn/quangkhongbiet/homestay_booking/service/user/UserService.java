package vn.quangkhongbiet.homestay_booking.service.user;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface UserService {
    Boolean existsById(Long id);

    Boolean existsByEmail(String email);
    
    User createUser(User user);

    Optional<User> findUserById(Long id);

    ResultPaginationDTO findAllUsers(Specification<User> spec, Pageable pageable);

    Optional<User> updatePartialUser(User user);

    void deleteUser(Long id);

    ResUserCreateDTO convertToResCreateUserDTO(User user);

    ResUserUpdatedDTO convertToResUpdatedUserDTO(User user);

    ResUserDTO convertToResUserDTO(User user);
}
