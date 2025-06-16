package vn.quangkhongbiet.homestay_booking.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ApiResponse;

@RestControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpResponse.getStatus();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(status);

        if (body instanceof String || body instanceof Resource) {
            return body;
        }
        // case error
        if (status >= 400) {
        	return body;
        } else {// case success
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            apiResponse.setData(body);
            apiResponse.setMessage(apiMessage != null ? apiMessage.value() : "CALL API SUCCESS");
            apiResponse.setTimestamp(java.time.Instant.now());
        }

        return apiResponse;
    }
}
