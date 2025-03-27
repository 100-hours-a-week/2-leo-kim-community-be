package org.community.config;

import lombok.RequiredArgsConstructor;
import org.community.aspect.CurrentPostWithUserArgumentResolver;
import org.community.aspect.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CurrentUserArgumentResolver currentUserArgumentResolver;
    private final CurrentPostWithUserArgumentResolver currentPostArgumentResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실제 파일 저장 경로
        registry.addResourceHandler("/upload/profiles/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/community/upload/profiles/");
        registry.addResourceHandler("/upload/post/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/community/upload/post/");

    }

    // ArgumentResolver 처리
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
        resolvers.add(currentPostArgumentResolver);
    }
}
