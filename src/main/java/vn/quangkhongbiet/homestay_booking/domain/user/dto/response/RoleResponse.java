package vn.quangkhongbiet.homestay_booking.domain.user.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;
    private Boolean active;
    private String description;
    private List<ResPermission> permissions;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResPermission {
        private Long id;
        private String apiPath;
        private String method;
        private String module;
    }
}
