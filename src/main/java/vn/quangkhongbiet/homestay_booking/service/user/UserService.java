package vn.quangkhongbiet.homestay_booking.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UpdateUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.ResUserUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface UserService {
    Boolean existsById(Long id);

    Boolean existsByEmail(String email);
    
    User createUser(User user);

    User findUserById(Long id);

    User getUserByEmail(String email);

    ResultPaginationDTO findAllUsers(Specification<User> spec, Pageable pageable);

    User updatePartialUser(UpdateUserDTO dto);

    void deleteUser(Long id);

    ResUserCreateDTO convertToResCreateUserDTO(User user);

    ResUserUpdatedDTO convertToResUpdatedUserDTO(User user);

    ResUserDTO convertToResUserDTO(User user);

    public void updateUserToken(String email, String token);

    public User getUserByRefreshTokenAndEmail(String token, String email);
}
