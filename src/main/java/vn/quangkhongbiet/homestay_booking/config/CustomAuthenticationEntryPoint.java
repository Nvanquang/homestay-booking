package vn.quangkhongbiet.homestay_booking.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // private final AuthenticationEntryPoint delegate = new
    // BearerTokenAuthenticationEntryPoint(); // default error

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        UnauthorizedException res = new UnauthorizedException(
                "Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...",
                "Authentication",
                "invalid_token");

        // Ghi lỗi ra body
        mapper.writeValue(response.getWriter(), res.getBody());
    }

}
