package org.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.community.annotation.CurrentPost;
import org.community.common.user.UserResponseMessage;
import org.community.entity.post.PostEntity;
import org.community.global.CustomException;
import org.community.respository.post.PostRepository;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrentPostArgumentResolver implements HandlerMethodArgumentResolver {
    private final PostRepository postRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentPost.class) &&
                parameter.getParameterType().equals(PostEntity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // request에서 경로를 가져와서
        // URI Template과 비교하여 추출
        String path = webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI();
        UriTemplate template = new UriTemplate("/posts/{postId}");
        Map<String, String> variables = template.match(path);
        String postIdStr = variables.get("postId");
        Long postId = Long.valueOf(postIdStr);

        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));
    }
}
