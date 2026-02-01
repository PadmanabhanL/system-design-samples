package com.sample.aws.dynamo.basic_crud.interceptor;

import com.sample.aws.dynamo.basic_crud.perf.MetricsContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class ResponseInterceptor implements WebRequestInterceptor {

    @Override
    public void preHandle(WebRequest request) throws Exception {
        System.out.println("Pre Handle");
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        System.out.println("postHandle");
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

        System.out.println("afterCompletion:" + MetricsContextHolder.getContextVariable("serverDuration"));
    }
}
