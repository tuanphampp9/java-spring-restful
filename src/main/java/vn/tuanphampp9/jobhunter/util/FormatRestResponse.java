package vn.tuanphampp9.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.tuanphampp9.jobhunter.domain.RestResponse;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice {

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // TODO Auto-generated method stub
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpServletResponse.getStatus();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);
        if (body instanceof String) {
            return body;
        }
        if (status >= 400) {
            // case error
            return body;
        } else {
            // case success
            res.setData(body);
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(apiMessage != null ? apiMessage.value() : "Api successfully");
        }
        return res;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // TODO Auto-generated method stub
        return true;
    }

}
