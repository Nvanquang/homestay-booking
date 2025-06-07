package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import java.net.URI;

public class EmailAlreadyUsedException extends BadRequestAlertException {

    public EmailAlreadyUsedException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, defaultMessage, entityName, errorKey);
    }
    
}
