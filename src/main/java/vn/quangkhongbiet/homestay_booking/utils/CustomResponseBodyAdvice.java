package vn.quangkhongbiet.homestay_booking.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.ApiResponse;

@RestControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        try {
            String path = ((ServletWebRequest) RequestContextHolder.getRequestAttributes())
                    .getRequest()
                    .getRequestURI();

            return !(path.startsWith("/v3/api-docs") ||
                     path.startsWith("/swagger") ||
                     path.startsWith("/swagger-ui") ||
                     path.startsWith("/actuator") ||
                     path.contains("api-docs"));
        } catch (Exception e) {
            return true; // fallback an toàn
        }
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

        if (body instanceof byte[] || body instanceof String || body instanceof Resource) {
            return body;
        }

        if (body instanceof vn.quangkhongbiet.homestay_booking.web.dto.response.ApiResponse) {
            return body;
        }

        // Tránh can thiệp vào các response không phải JSON như Swagger
        if (selectedContentType != null && !selectedContentType.includes(MediaType.APPLICATION_JSON)) {
            return body;
        }

        if (status >= 400) {
            return body;
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(status);
        apiResponse.setData(body);
        apiResponse.setMessage("CALL API SUCCESS");
        apiResponse.setTimestamp(java.time.Instant.now());

        ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
        if (apiMessage != null) {
            apiResponse.setMessage(apiMessage.value());
        }

        return apiResponse;
    }
}
