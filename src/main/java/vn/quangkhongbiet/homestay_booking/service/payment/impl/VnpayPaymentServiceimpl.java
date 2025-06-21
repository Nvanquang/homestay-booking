package vn.quangkhongbiet.homestay_booking.service.payment.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.payment.constant.VNPayParams;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.request.InitPaymentRequest;
import vn.quangkhongbiet.homestay_booking.domain.payment.dto.response.InitPaymentResponse;
import vn.quangkhongbiet.homestay_booking.domain.payment.entity.PaymentTransaction;
import vn.quangkhongbiet.homestay_booking.repository.PaymentTransactionRepository;
import vn.quangkhongbiet.homestay_booking.service.payment.VnpayPaymentService;
import vn.quangkhongbiet.homestay_booking.utils.VnpayUtil;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VnpayPaymentServiceimpl implements VnpayPaymentService {

    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";
    public static final String ORDER_TYPE = "190000";
    public static final long DEFAULT_MULTIPLIER = 100L;

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;

    @Value("${payment.vnpay.vnp_PayUrl}")
    private String vnpPayurl;

    @Value("${payment.vnpay.vnp_ReturnUrl}")
    private String vnpReturnUrl;

    @Value("${payment.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${payment.vnpay.timeout}")
    private Integer paymentTimeout;

    private final PaymentTransactionRepository paymentRepository;


    @Override
    public InitPaymentResponse init(InitPaymentRequest request) {
        long amount = request.getAmount() * DEFAULT_MULTIPLIER; // khử số thập phân
        String vnp_TxnRef = request.getTxnRef();                // booking id
        String vnp_IpAddr = request.getIpAddress();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        cld.add(Calendar.MINUTE, paymentTimeout);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        String requestId = request.getRequestId(); 
        
        Map<String, String> vnp_Params = new HashMap<>();

        vnp_Params.put(VNPayParams.VERSION, VERSION);
        vnp_Params.put(VNPayParams.COMMAND, COMMAND);

        vnp_Params.put(VNPayParams.TMN_CODE, tmnCode);
        vnp_Params.put(VNPayParams.AMOUNT, String.valueOf(amount));
        vnp_Params.put(VNPayParams.CURRENCY, "VND");
        
      
        vnp_Params.put(VNPayParams.TXN_REF, vnp_TxnRef);
        vnp_Params.put(VNPayParams.RETURN_URL, vnpReturnUrl);

        vnp_Params.put(VNPayParams.ORDER_INFO, "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put(VNPayParams.ORDER_TYPE, ORDER_TYPE);

        vnp_Params.put(VNPayParams.LOCALE, "vn");
        vnp_Params.put(VNPayParams.IP_ADDRESS, vnp_IpAddr);

        vnp_Params.put(VNPayParams.CREATED_DATE, vnp_CreateDate);
        vnp_Params.put(VNPayParams.EXPIRE_DATE, vnp_ExpireDate);

        String initPaymentUrl = this.buildInitPaymentUrl(vnp_Params);
        log.debug("[request_id={}] Init payment url: {}", requestId, initPaymentUrl);

        return InitPaymentResponse.builder()
                .vnpUrl(initPaymentUrl)
                .build();
    }

    @SneakyThrows
    private String buildInitPaymentUrl(Map<String, String> vnp_Params){
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames); // sort field name
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        // build secure hash
        String vnp_SecureHash = VnpayUtil.hmacSHA512(secretKey, hashData.toString());

        // Finalize query
        String queryUrl = query.toString();
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpPayurl + "?" + queryUrl;

        return paymentUrl;
    }

    @Override
    public boolean existsById(Long id){
        return this.paymentRepository.existsById(id);
    }

    @Override
    public PaymentTransaction findById(Long id){
        return this.paymentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("PaymentTransaction not found", "payment", "paymentnotfound"));
    }

    @Override
    public PagedResponse getAll(Specification<PaymentTransaction> spec, Pageable pageable){
        log.debug("find all PaymentTransaction with spec: {}, pageable: {}", spec, pageable);
        Page<PaymentTransaction> pagePaymentTransactions = this.paymentRepository.findAll(spec, pageable);
        PagedResponse result = new PagedResponse();
        PagedResponse.Meta meta = result.new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pagePaymentTransactions.getTotalPages());
        meta.setTotal(pagePaymentTransactions.getTotalElements());

        result.setMeta(meta);
        result.setResult(pagePaymentTransactions.getContent());
        return result;
    }
}
