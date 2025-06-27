package vn.quangkhongbiet.homestay_booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Homestay Booking API")
                .version("1.0.0")
                .description("""

                    ```json
                    {
                      "status": 200,
                      "message": "CALL API SUCCESS",
                      "timestamp": "2025-06-26T14:00:00Z",
                      "data": {
                        // Dữ liệu thực tế trả về
                      }
                    }
                    ```

                    """)
                .contact(new Contact()
                    .name("Quang Nguyen")
                    .email("quang@example.com")));
    }
}

