package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResultPaginationDTO;

public interface HomestayService {
    boolean existsById(long id);

    Homestay createHomestay(Homestay homestay);

    Optional<Homestay> findHomestayById(Long id);

    ResultPaginationDTO findAllHomestays(Specification<Homestay> spec, Pageable pageable);

    Optional<Homestay> updatePartialHomestay(Homestay homestay);

    void deleteHomestay(Long id);

    ResHomestayCreateDTO convertToResCreateHomestayDTO(Homestay homestay);

    ResHomestayUpdatedDTO convertToResUpdatedHomestayDTO(Homestay homestay);

    ResHomestayDTO convertToResHomestayDTO(Homestay homestay);
}
