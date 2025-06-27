package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "http://localhost8080:/problem";
    
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final URI MULTIPART_EXCEPTION_TYPE = URI.create(PROBLEM_BASE_URL + "/multipart-error");

    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create(PROBLEM_BASE_URL + "/entity-not-found");
    public static final URI UNAUTHORIZED = URI.create(PROBLEM_BASE_URL + "/unauthorized");
    public static final URI FORBIDDEN = URI.create(PROBLEM_BASE_URL + "/forbidden");
    public static final URI CONFLICT = URI.create(PROBLEM_BASE_URL + "/conflict");

    private ErrorConstants() {
    }
}
