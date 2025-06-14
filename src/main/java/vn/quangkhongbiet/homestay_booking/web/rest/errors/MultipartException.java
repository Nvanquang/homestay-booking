package vn.quangkhongbiet.homestay_booking.web.rest.errors;

public class MultipartException extends BadRequestAlertException{
    MultipartException(String message) {
        super(ErrorConstants.MULTIPART_EXCEPTION_TYPE, message, "HomestayImage", "multipartfileerror");
    }
}
