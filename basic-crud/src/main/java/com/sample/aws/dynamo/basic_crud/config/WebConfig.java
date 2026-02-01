package com.sample.aws.dynamo.basic_crud.config;

import com.sample.aws.dynamo.basic_crud.interceptor.ResponseInterceptor;
import com.sample.aws.dynamo.basic_crud.interceptor.ResponseInterceptorAdaptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ResponseInterceptorAdaptor(new ResponseInterceptor()));
    }
}
