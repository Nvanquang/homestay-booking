package vn.quangkhongbiet.homestay_booking.domain.user.dto.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterCheckedValidator.class)
public @interface RegisterChecked {
    String message() default "User register validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
