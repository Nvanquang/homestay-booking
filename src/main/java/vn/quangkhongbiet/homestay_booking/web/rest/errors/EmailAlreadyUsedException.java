package vn.quangkhongbiet.homestay_booking.web.rest.errors;

public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "The email has already been used!", "userManagement", "emailexists");
    }
    
}
