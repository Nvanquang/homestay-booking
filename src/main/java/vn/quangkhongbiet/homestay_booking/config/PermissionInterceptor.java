package vn.quangkhongbiet.homestay_booking.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.ForbiddenException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    private static final Map<String, String> PUBLIC_GET_APIS = Map.ofEntries(
        Map.entry("/", "GET"),
        Map.entry("/api/v1/amenities/{id}", "GET"),
        Map.entry("/api/v1/amenities", "GET"),
        Map.entry("/api/v1/homestays/{id}", "GET"),
        Map.entry("/api/v1/homestays/search", "GET"),
        Map.entry("/api/v1/homestays", "GET"),
        Map.entry("/api/v1/locations/{id}", "GET"),
        Map.entry("/api/v1/payments/vnpay_ipn", "GET")
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        // 1. Bỏ qua nếu không phải là handler method (VD: static resources, Swagger, v.v.)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();
        AntPathMatcher matcher = new AntPathMatcher();

        // 2. Cho phép truy cập nếu là API công khai
        for (Map.Entry<String, String> entry : PUBLIC_GET_APIS.entrySet()) {
            if (matcher.match(entry.getKey(), requestPath) && entry.getValue().equalsIgnoreCase(requestMethod)) {
                return true;
            }
        }

        // 3. Xác thực người dùng
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found or not authenticated", "Authentication", "usernotfound"));

        if (user.getRole() == null) {
            throw new ForbiddenException();
        }

        // 4. Phân quyền (Authorization)
        List<Permission> permissions = user.getRole().getPermissions();
        boolean hasPermission = permissions.stream()
                .anyMatch(p -> matcher.match(p.getApiPath(), requestPath) && p.getMethod().equalsIgnoreCase(requestMethod));

        if (!hasPermission) {
            throw new ForbiddenException();
        }

        return true;
    }
}
