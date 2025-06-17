package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

public class ForbiddenException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException() {
        super(
            HttpStatus.FORBIDDEN,
            ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.FORBIDDEN.value())
                .withType(ErrorConstants.FORBIDDEN)
                .withDetail("Access denied: no role assigned")
                .build(),
            null
        );
    }
}

