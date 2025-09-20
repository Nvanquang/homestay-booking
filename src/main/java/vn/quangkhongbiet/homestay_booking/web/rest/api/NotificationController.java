package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterFcmTokenRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.UnregisterFcmTokenRequest;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterFcmTokenRequest req) {
        if (req.getUserId() == null || req.getToken() == null)
            return ResponseEntity.badRequest().build();
        this.userRepository.findByIdAndFcmToken(req.getUserId(), req.getToken()).map(existingUser -> {
            // Token đã tồn tại, không cần cập nhật
            return existingUser;
        }).orElseGet(() -> {
            // Cập nhật token mới
            return this.userRepository.findById(req.getUserId()).map(user -> {
                user.setFcmToken(req.getToken());
                return this.userRepository.save(user);
            }).orElse(null);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> unregister(@RequestBody UnregisterFcmTokenRequest req) {
        if (req.getToken() == null)
            return ResponseEntity.badRequest().build();
        User userDB = this.userRepository.findByFcmToken(req.getToken());
        if (userDB != null) {
            userDB.setFcmToken(null);
            this.userRepository.save(userDB);
        }
        return ResponseEntity.ok().build();
    }
}
