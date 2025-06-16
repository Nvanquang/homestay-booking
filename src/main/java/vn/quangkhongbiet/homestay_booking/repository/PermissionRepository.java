package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsById(Long id);
    
    boolean existsByApiPathAndMethodAndModule(String apiPath, String method, String module);

    List<Permission> findByIdIn(List<Long> ids);
}
