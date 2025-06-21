package vn.quangkhongbiet.homestay_booking.service.payment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.payment.dto.request.InitPaymentRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.InitPaymentResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.entity.PaymentTransaction;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

public interface VnpayPaymentService {
    InitPaymentResponse init(InitPaymentRequest request);

    boolean existsById(Long id);

    PaymentTransaction findById(Long id);

    PagedResponse getAll(Specification<PaymentTransaction> spec, Pageable pageable);
}
