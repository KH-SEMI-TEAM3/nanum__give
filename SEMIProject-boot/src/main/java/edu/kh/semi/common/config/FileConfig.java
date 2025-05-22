package edu.kh.semi.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:config.properties")
public class FileConfig implements WebMvcConfigurer {

    @Value("${my.profile.resource-handler}")
    private String profileResourceHandler;

    @Value("${my.profile.resource-location}")
    private String profileResourceLocation;
    
    
    
    
    
    
    
    
	
	// 게시판 이미지 관련 경로
	
	@Value("${my.board.resource-handler}")
	private String boardResourceHandler;
	
	@Value("${my.board.resource-location}")
	private String boardResourceLocation;
	
    
    
    
    
    
    
    
    
    
    
    
    
    


    // 정적 리소스 핸들링 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(profileResourceHandler)
            .addResourceLocations(profileResourceLocation);
        
        
        registry.addResourceHandler(boardResourceHandler)
        .addResourceLocations(boardResourceLocation);
    }
    
    
}