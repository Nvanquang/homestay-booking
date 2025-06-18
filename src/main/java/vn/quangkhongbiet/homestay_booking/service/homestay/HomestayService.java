package vn.quangkhongbiet.homestay_booking.service.homestay;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqHomestaySearch;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.UpdateHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayCreateDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResHomestayUpdatedDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResSearchHomestayDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface HomestayService {
    Boolean existsById(Long id);

    Homestay createHomestay(Homestay homestay, MultipartFile[] files, String folder);

    Homestay addAmenitiesToHomestay(long homestayId, List<Long> amenityIds);

    Homestay findHomestayById(Long id);

    List<ResSearchHomestayDTO> searchHomestays(ReqHomestaySearch request);

    PagedResponse findAllHomestays(Specification<Homestay> spec, Pageable pageable);

    Homestay updatePartialHomestay(UpdateHomestayDTO homestay);

    void deleteHomestay(Long id);

    ResHomestayCreateDTO convertToResCreateHomestayDTO(Homestay homestay);

    ResHomestayUpdatedDTO convertToResUpdatedHomestayDTO(Homestay homestay);

    ResHomestayDTO convertToResHomestayDTO(Homestay homestay);
}
