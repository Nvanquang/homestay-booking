package vn.quangkhongbiet.homestay_booking.domain.payment.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.quangkhongbiet.homestay_booking.domain.booking.entity.Booking;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId; // Mã giao dịch từ cổng thanh toán

    @Column(name = "status")
    private String status; // 00: PENDING, 01: SUCCESS, 02: FAILED, ...

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "response_message")
    private String responseMessage; // lưu thông báo từ gateway

    @Column(name = "request_id")
    private String requestId; 

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    /**
     * LƯU Ý:
     * Hiện tại quan hệ giữa Booking và PaymentTransaction được thiết kế là One-to-One (1-1)
     * để đơn giản hoá logic trong giai đoạn phát triển và testing. Tuy nhiên, trong môi trường 
     * thực tế, mối quan hệ này nên được chuyển sang One-to-Many (n-1) vì các lý do sau:
     *
     * 1. Hỗ trợ retry khi thanh toán thất bại: Một Booking có thể có nhiều lần thử thanh toán.
     * 2. Giao dịch có thể timeout hoặc lỗi tạm thời từ phía cổng thanh toán → cần ghi nhận nhiều transaction.
     * 3. Hỗ trợ thanh toán nhiều đợt: ví dụ đặt cọc và thanh toán phần còn lại sau đó.
     * 4. Cần lưu lịch sử đầy đủ các giao dịch phục vụ mục đích truy vết, audit, phân tích lỗi.
     *
     *  Nếu triển khai thực tế, nên refactor quan hệ này thành:
     *     - Booking: @OneToMany(mappedBy = "booking")
     *     - PaymentTransaction: @ManyToOne
     */

}