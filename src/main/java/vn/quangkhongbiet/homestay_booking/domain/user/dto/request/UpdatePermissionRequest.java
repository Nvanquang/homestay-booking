package vn.quangkhongbiet.homestay_booking.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {
    
    @NotNull(message = "Permission ID is required")
    @Positive(message = "Permission ID must be positive")
    private Long id;
    
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 255, message = "API path cannot exceed 255 characters")
    private String apiPath;
    
    @Pattern(regexp = "^(GET|POST|PATCH|DELETE)$", message = "Method must be GET, POST, PATCH or DELETE")
    private String method;
    
    @Size(max = 100, message = "Module name cannot exceed 100 characters")
    private String module;
}
