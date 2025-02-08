package com.study.mms.config;

import com.study.mms.Filter.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<XSSFilter> xssFilterRegistration() {
        FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XSSFilter()); // XSS 필터 적용
        registrationBean.addUrlPatterns("/*"); // 모든 URL 패턴에 필터 적용
        registrationBean.setOrder(1); // 필터 실행 순서 (낮을수록 먼저 실행됨)
        return registrationBean;
    }

}
