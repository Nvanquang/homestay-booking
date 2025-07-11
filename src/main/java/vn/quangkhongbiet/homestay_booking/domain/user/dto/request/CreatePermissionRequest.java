package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePermissionRequest {
    
    @NotNull(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "API path is required")
    @Size(max = 255, message = "API path cannot exceed 255 characters")
    private String apiPath;

    @NotNull(message = "Method is required")
    @Pattern(regexp = "^(GET|POST|PATCH|DELETE)$", message = "Method must be GET, POST, PATCH hoặc DELETE")
    private String method;

    @Size(max = 100, message = "Module name cannot exceed 100 characters")
    private String module;
}
