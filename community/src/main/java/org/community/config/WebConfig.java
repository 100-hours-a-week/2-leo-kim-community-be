package org.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실제 파일 저장 경로
        registry.addResourceHandler("/upload/profiles/**","/upload/post/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/community/upload/profiles/");
    }
}
