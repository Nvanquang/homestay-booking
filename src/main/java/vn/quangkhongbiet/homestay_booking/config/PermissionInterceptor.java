package vn.quangkhongbiet.homestay_booking.config;

import java.io.IOException;
import java.util.List;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        // Bỏ qua static resource hoặc nếu không phải là handler method (vd: Swagger UI)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // Lấy email người dùng từ SecurityContext
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found or not authenticated", "Authentication", "usernotfound"));

        if (user.getRole() == null) {
            throw new ForbiddenException();
            // return false;
        }

        // Lấy danh sách permission từ role
        List<Permission> permissions = user.getRole().getPermissions();

        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        boolean hasPermission = permissions.stream()
                .anyMatch(p -> requestPathMatches(p.getApiPath(), requestPath) && p.getMethod().equals(requestMethod));


        if (!hasPermission) {
            throw new ForbiddenException();
        }

        return true;
    }

    // So sánh path có thể chứa {id}
    private boolean requestPathMatches(String pattern, String actualPath) {
        // Sử dụng AntPathMatcher cho khớp pattern như /users/{id}
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, actualPath);
    }
}