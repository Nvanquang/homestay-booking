package vn.quangkhongbiet.homestay_booking.web.rest.errors;

import java.net.URI;

public class BusinessException extends BadRequestAlertException{

    public BusinessException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, defaultMessage, entityName, errorKey);
    }
    
}
