package vn.quangkhongbiet.homestay_booking.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserDTO;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.OtpPayload;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpGenerator otpGenerator;

    private final MailService mailService;


    /**
     * Method for generate OTP number
     *
     * @param key - provided key (username in this case)
     */
    public void generateOtp(RegisterUserDTO register)
    {
        // generate otp
        Integer otpValue = otpGenerator.generateOTP(register.getEmail());
        if (otpValue == -1)
        {
            log.warn("OTP generator is not working...");
        }


        log.debug("Generated OTP: {}", otpValue);

         OtpPayload otpPayload = OtpPayload.builder()
                        .email(register.getEmail())
                        .fullName(register.getFullName())
                        .otp(otpValue.toString())
                        .build();
        mailService.sendOtpEmail(otpPayload);
    }

    /**
     * Method for validating provided OTP
     *
     * @param key - provided key
     * @param otpNumber - provided OTP number
     * @return boolean value (true|false)
     */
    public Boolean validateOTP(String email, String otpNumber)
    {
        // get OTP from cache
        String cacheOTP = otpGenerator.getOTPByKey(email);
        if (cacheOTP!=null && cacheOTP.equals(otpNumber))
        {
            otpGenerator.clearOTPFromCache(email);
            return true;
        }
        return false;
    }
}

