package com.lvhao.nowcodercommunity.config;

import com.lvhao.nowcodercommunity.constant.AppConstants;
import com.lvhao.nowcodercommunity.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor)
                .excludePathPatterns(AppConstants.LOGIN_NO_INTERCEPT_URL_LIST);
    }
}
