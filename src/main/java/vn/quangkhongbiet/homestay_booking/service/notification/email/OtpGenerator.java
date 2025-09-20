package vn.quangkhongbiet.homestay_booking.service.notification.email;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.TooManyRequestsException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;

@Slf4j
@Service
public class OtpGenerator {

    private static final int EXPIRE_MIN = 5;

    private static final int CACHE_MAX_SIZE = 10000;

    private static final int MAX_OTP_ATTEMPTS = 5; // 5 OTPs per 5 minutes

    private static final SecureRandom random = new SecureRandom();

    private final LoadingCache<String, String> otpCache;

    private final Map<String, Bucket> rateLimitBuckets; // Store buckets per user

    public OtpGenerator() {
        otpCache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String email) {
                        throw new IllegalStateException("No OTP available for email: " + email);
                    }
                });

        rateLimitBuckets = new ConcurrentHashMap<>();
    }

    /**
     * Creates a rate limit bucket for a given email (e.g., username).
     */
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(MAX_OTP_ATTEMPTS, Refill.greedy(MAX_OTP_ATTEMPTS, Duration.ofMinutes(EXPIRE_MIN)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Generates OTP and stores it in cache, with rate limiting.
     *
     * @param email - cache email (e.g., username)
     * @return generated OTP number
     * @throws BadRequestAlertException if email is null or empty
     * @throws TooManyRequestsException if OTP generation limit is exceeded
     */
    public Integer generateOTP(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestAlertException("Email cannot be null or empty", "otp", "invalid");
        }

        // Get or create bucket for the email
        Bucket bucket = rateLimitBuckets.computeIfAbsent(email, k -> createBucket());

        // Check rate limit
        if (!bucket.tryConsume(1)) {
            log.warn("OTP generation limit exceeded for email: {}", email);
            throw new TooManyRequestsException("Too many OTP generation attempts. Please try again later.", "otp", "tooManyRequests");
        }

        Integer otp = 100000 + random.nextInt(900000);
        otpCache.put(email, otp.toString());
        log.info("Generated OTP for email: {}, otp: {}", email, otpCache.getIfPresent(email));
        return otp;
    }

    /**
     * Gets OTP value by email.
     *
     * @param email - target email
     * @return OTP value
     * @throws BadRequestAlertException if email is null or empty
     * @throws EntityNotFoundException if OTP not found or expired
     */
    public String getOTPByKey(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestAlertException("Email cannot be null or empty", "otp", "invalid");
        }
        String otp = otpCache.getIfPresent(email);
        if (otp == null) {
            log.warn("OTP not found or expired for email: {}", email);
            throw new EntityNotFoundException("OTP not found or expired for email: " + email, "otp", "notFound");
        }
        return otp;
    }

    /**
     * Removes email from cache and rate limit bucket.
     *
     * @param email - target email
     * @throws BadRequestAlertException if email is null or empty
     */
    public void clearOTPFromCache(String email) {
        if (email == null || email.isEmpty()) {
            throw new BadRequestAlertException("Email cannot be null or empty", "otp", "invalid");
        }
        otpCache.invalidate(email);
        rateLimitBuckets.remove(email); // Remove bucket to reset rate limit
        log.debug("Cleared OTP and rate limit for email: {}", email);
    }
}