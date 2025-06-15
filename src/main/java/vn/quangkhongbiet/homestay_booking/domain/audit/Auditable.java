package vn.quangkhongbiet.homestay_booking.domain.audit;

import java.time.Instant;

public interface Auditable {
    void setCreatedAt(Instant createdAt);
    void setUpdatedAt(Instant updatedAt);
    void setCreatedBy(String createdBy);
    void setUpdatedBy(String updatedBy);
}
