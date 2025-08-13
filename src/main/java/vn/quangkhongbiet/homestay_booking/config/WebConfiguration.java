package vn.quangkhongbiet.homestay_booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các endpoint

            // Danh sách origins được phép
            .allowedOrigins("http://localhost:3000", "http://localhost:4173", "http://localhost:5173")

            // Các phương thức HTTP được phép
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")

            // Cho phép tất cả headers
            .allowedHeaders("Authorization", "Content-Type", "Accept", "x-no-retry")

            // Cho phép gửi cookie hoặc thông tin xác thực
            .allowCredentials(true)

            // Thời gian cache CORS preflight request (giây)
            .maxAge(3600);
    }
}
