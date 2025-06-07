package vn.quangkhongbiet.homestay_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
