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
    public static final URI HOMESTAY_NOT_ACTIVE = URI.create(PROBLEM_BASE_URL + "/homestay-not-active");
    public static final URI GUESTS_INVALID = URI.create(PROBLEM_BASE_URL + "/guests-invalid");
    public static final URI CHECKIN_DATE_INVALID = URI.create(PROBLEM_BASE_URL + "/checkin-date-invalid");
    public static final URI NIGHTS_INVALID = URI.create(PROBLEM_BASE_URL + "/nights-invalid");
    public static final URI HOMESTAY_BUSY = URI.create(PROBLEM_BASE_URL + "/homestay-busy");

    public static final URI UNAUTHORIZED = URI.create(PROBLEM_BASE_URL + "/unauthorized");

    private ErrorConstants() {
    }
}
