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
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ResponseDTO;

@RestControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Only wrap if not already an ProblemDetailWithCause
        return !returnType.getParameterType().equals(ProblemDetailWithCause.class);
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

        ResponseDTO apiResponse = new ResponseDTO();
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
