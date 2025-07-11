package vn.quangkhongbiet.homestay_booking.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.response.OtpEmailPayload;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending emails asynchronously.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String USER = "user";

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        this.sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    public void sendEmailFromTemplateSync(User user, String subject, String templateName) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getUserName());
            return;
        }
        Context context = new Context();
        context.setVariable(USER, user);
        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailOtpFromTemplate(OtpEmailPayload otp, String templateName, String titleKey) {
        this.sendEmailOtpFromTemplateSync(otp, templateName, titleKey);
    }

    public void sendEmailOtpFromTemplateSync(OtpEmailPayload otp, String subject, String templateName) {
        if (otp.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", otp.getFullName());
            return;
        }
        Context context = new Context();
        context.setVariable(USER, otp);
        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(otp.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendOtpEmail(OtpEmailPayload otp) {
        log.debug("Sending OTP email to '{}'", otp.getEmail());
        this.sendEmailOtpFromTemplateSync(otp, "email.otp.title", "mail/otpEmail");
    }
}
