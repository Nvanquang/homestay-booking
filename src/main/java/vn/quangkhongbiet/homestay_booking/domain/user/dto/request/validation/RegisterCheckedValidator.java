package vn.quangkhongbiet.homestay_booking.domain.user.dto.request.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.quangkhongbiet.homestay_booking.domain.user.dto.request.RegisterUserRequest;

public class RegisterCheckedValidator implements ConstraintValidator<RegisterChecked, RegisterUserRequest> {

    @Override
    public void initialize(RegisterChecked constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RegisterUserRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle this if needed
        }

        String password = value.getPassword();
        String confirmPassword = value.getConfirmPassword();

        // Check if userName is null or empty (handled by @NotNull if added)
        if (value.getUserName() == null || value.getUserName().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Username cannot be null or empty")
                    .addPropertyNode("userName").addConstraintViolation();
            return false;
        }
        
        // Check if password is null or empty (handled by @NotNull if added)
        if (password == null || confirmPassword == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password and confirmPassword cannot be null")
                    .addConstraintViolation();
            return false;
        }

        // Check password length (at least 8 characters)
        if (password.length() < 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters long")
                    .addPropertyNode("password").addConstraintViolation();
            return false;
        }

        // Basic password strength check (contains letters and numbers)
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one letter and one number")
                    .addPropertyNode("password").addConstraintViolation();
            return false;
        }

        // Check if confirmPassword matches password
        if (!password.equals(confirmPassword)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password and confirmPassword must match")
                    .addPropertyNode("confirmPassword").addConstraintViolation();
            return false;
        }

        return true;
    }
}