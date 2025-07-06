package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

public class TooManyRequestsException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public TooManyRequestsException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.TOO_MANY_REQUESTS, defaultMessage, entityName, errorKey);
    }

    public TooManyRequestsException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
            HttpStatus.TOO_MANY_REQUESTS,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.TOO_MANY_REQUESTS.value())
                .withType(type)
                .withDetail(defaultMessage)
                .withProperty("message", "error." + errorKey)
                .withProperty("params", entityName)
                .build(),
            null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
