package vn.quangkhongbiet.homestay_booking.domain.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import vn.quangkhongbiet.homestay_booking.utils.SecurityUtil;

import java.time.Instant;

public class AuditTrailListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setCreatedAt(Instant.now());
            auditable.setCreatedBy(SecurityUtil.getCurrentUserLogin().orElse("SYSTEM"));
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setUpdatedAt(Instant.now());
            auditable.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("SYSTEM"));
        }
    }
}
