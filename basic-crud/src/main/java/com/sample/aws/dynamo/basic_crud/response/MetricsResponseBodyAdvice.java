package com.sample.aws.dynamo.basic_crud.response;

import com.sample.aws.dynamo.basic_crud.perf.MetricsContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MetricsResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        System.out.println("beforeBodyWrite:" + MetricsContextHolder.getContextVariable("requestStartTime"));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", body);
        responseBody.put("metrics", getMetricsContext());
        return responseBody;

    }

    private Map<String, Object> getMetricsContext() {
        Map<String, Object> metrics = new HashMap<>();
        long startTime = (long) MetricsContextHolder.getContextVariable("requestStartTime");
        metrics.put("serverDuration", System.currentTimeMillis() - startTime);
        metrics.put("sqlDuration", MetricsContextHolder.getContextVariable("sqlDuration"));
        return metrics;
    }
}
