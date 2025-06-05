package edu.kh.semi.common.config;

import edu.kh.semi.common.interceptor.MemberInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 인터셉터가 어떤 요청을 가로챌지를 설정한다
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private MemberInterceptor memberInterceptor; // Bean 자동 주입

    @Override
    public void addInterceptors(InterceptorRegistry registry) { // 하나의 인터셉터 추가
        registry.addInterceptor(memberInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                );
    }
}