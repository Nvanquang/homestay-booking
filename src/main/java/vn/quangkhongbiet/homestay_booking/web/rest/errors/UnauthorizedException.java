package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;


public class UnauthorizedException extends ErrorResponseException {
    private final String entityName;

    private final String errorKey;

    public UnauthorizedException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.UNAUTHORIZED, defaultMessage, entityName, errorKey);
    }

    public UnauthorizedException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
            HttpStatus.UNAUTHORIZED,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.UNAUTHORIZED.value())
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

    
}
