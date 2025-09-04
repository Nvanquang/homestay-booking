package vn.quangkhongbiet.homestay_booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
                http
                        .cors(Customizer.withDefaults())
                        .csrf(c -> c.disable())
                        .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/api/v1/auth/**", "/vnpay", "/swagger-ui/**","/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                .requestMatchers(HttpMethod.GET,
                                        "/api/v1/payments/vnpay_ipn",
                                        "/api/v1/amenities/{id}",
                                        "/api/v1/amenities",
                                        "/api/v1/homestays/{id}",
                                        "/api/v1/homestays/search",
                                        "/api/v1/homestays",
                                        "/api/v1/locations/{id}",
                                        "/api/v1/reviews/homestay/{homestayId}",
                                        "/api/v1/availabilities",
                                        "/chat/**"
                                        ).permitAll()
                                .anyRequest().authenticated()
                        )
                        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                        .formLogin(f -> f.disable())
                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

}
