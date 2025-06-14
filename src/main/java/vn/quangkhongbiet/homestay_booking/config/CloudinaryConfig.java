package vn.quangkhongbiet.homestay_booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String YOUR_CLOUD_NAME;
    @Value("${cloudinary.api_key}")
    private String YOUR_API_KEY;
    @Value("${cloudinary.api_secret}")
    private String YOUR_API_SECRET;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", YOUR_CLOUD_NAME);
        config.put("api_key", YOUR_API_KEY);
        config.put("api_secret", YOUR_API_SECRET);
        return new Cloudinary(config);
    }
}
