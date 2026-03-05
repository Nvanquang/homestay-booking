package vn.quangkhongbiet.homestay_booking.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseConfigurarion {

    @Value("${quangkhongbiet.firebase.config-path}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        GoogleCredentials credentials;

        // First check if credentials are provided via environment variable (useful for
        // Render/Render)
        String firebaseCredentialsJson = System.getenv("FIREBASE_CREDENTIALS");
        if (firebaseCredentialsJson != null && !firebaseCredentialsJson.trim().isEmpty()) {
            java.io.ByteArrayInputStream serviceAccountStream = new java.io.ByteArrayInputStream(
                    firebaseCredentialsJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        } else {
            // Fall back to reading from file
            FileInputStream serviceAccountFile = new FileInputStream(firebaseConfigPath);
            credentials = GoogleCredentials.fromStream(serviceAccountFile);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
        return FirebaseMessaging.getInstance(app);
    }
}
