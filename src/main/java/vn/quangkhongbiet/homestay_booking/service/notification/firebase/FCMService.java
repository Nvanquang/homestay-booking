package vn.quangkhongbiet.homestay_booking.service.notification.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    private final UserRepository userRepository;

    public void sendSimple(String token, String title, String body) {
        try {
            WebpushNotification notif = WebpushNotification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setIcon("/vite.svg") // tùy chỉnh icon
                    .build();

            WebpushConfig webpush = WebpushConfig.builder()
                    .setNotification(notif)
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setWebpushConfig(webpush)
                    .build();

            String id = firebaseMessaging.send(message);
            log.info("FCM sent: {}", id);
        } catch (Exception e) {
            log.error("FCM send error: {}", e.getMessage(), e);
            // Nếu lỗi NotRegistered/InvalidRegistration -> xóa token
            if (e.getMessage() != null
                    && (e.getMessage().contains("NotRegistered") || e.getMessage().contains("InvalidRegistration"))) {
                User userDB = this.userRepository.findByFcmToken(token);
                userDB.setFcmToken(null);
                this.userRepository.save(userDB);
            }
        }
    }

    // public void sendSimpleToUsers(List<Long> userIds, String title, String body) {
    //     List<String> tokens = userRepository.findFcmTokensByUserIds(userIds);
    //     if (tokens.isEmpty())
    //         return;

    //     WebpushNotification notif = WebpushNotification.builder()
    //             .setTitle(title)
    //             .setBody(body)
    //             .setIcon("/favicon.ico")
    //             .build();

    //     WebpushConfig webpush = WebpushConfig.builder()
    //             .setNotification(notif)
    //             .build();

    //     MulticastMessage message = MulticastMessage.builder()
    //             .addAllTokens(tokens)
    //             .setWebpushConfig(webpush)
    //             .build();

    //     try {
    //         BatchResponse res = firebaseMessaging.sendMulticast(message);
    //         log.info("FCM multicast: success {}/{}", res.getSuccessCount(), tokens.size());
    //         for (int i = 0; i < res.getResponses().size(); i++) {
    //             SendResponse r = res.getResponses().get(i);
    //             if (!r.isSuccessful()) {
    //                 String badToken = tokens.get(i);
    //                 String err = String.valueOf(r.getException());
    //                 if (err.contains("NotRegistered") || err.contains("InvalidRegistration")) {
    //                     User userDB = this.userRepository.findByFcmToken(badToken);
    //                     userDB.setFcmToken(null);
    //                     this.userRepository.save(userDB);
    //                 }
    //             }
    //         }
    //     } catch (Exception e) {
    //         log.error("FCM multicast error", e);
    //     }
    // }
}