package vn.quangkhongbiet.homestay_booking.service.payment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.quangkhongbiet.homestay_booking.domain.payment.dto.request.InitPaymentRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.InitPaymentResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.entity.PaymentTransaction;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;

/**
 * Service interface for managing VNPay payment transactions.
 * Provides methods for initializing payments and querying payment transactions.
 */
public interface VnpayPaymentService {
    /**
     * Initialize a VNPay payment.
     * @param request the payment initialization request.
     * @return the payment initialization response.
     */
    InitPaymentResponse init(InitPaymentRequest request);

    /**
     * Check if a payment transaction exists by id.
     * @param id the transaction id.
     * @return true if exists, false otherwise.
     */
    boolean existsById(Long id);

    /**
     * Find a payment transaction by id.
     * @param id the transaction id.
     * @return the payment transaction entity.
     * @throws EntityNotFoundException if payment transaction not found.
     */
    PaymentTransaction findById(Long id);

    /**
     * Get all payment transactions with specification and pagination.
     * @param spec the specification.
     * @param pageable the pagination info.
     * @return paged response of payment transactions.
     */
    PagedResponse findAll(Specification<PaymentTransaction> spec, Pageable pageable);
}
