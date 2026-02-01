package com.sample.aws.dynamo.basic_crud.filter;

import com.sample.aws.dynamo.basic_crud.perf.MetricsContextHolder;
import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class MetricsFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("Inside filter");
        MetricsContextHolder.putContextVariable("requestStartTime", System.currentTimeMillis());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            long requestStartTime = (Long) MetricsContextHolder.getContextVariable("requestStartTime");
            long requestEndTime = System.currentTimeMillis();
            System.out.println("Finally Called");
            MetricsContextHolder.putContextVariable("serverDuration", requestEndTime - requestStartTime);
        }

    }
}
