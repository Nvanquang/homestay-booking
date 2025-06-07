package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long>, JpaSpecificationExecutor<Homestay> {
    boolean existsById(long id);

    List<Homestay> findByIdIn(List<Long> ids);
}
