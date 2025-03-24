package org.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.community.annotation.CurrentUser;
import org.community.common.user.UserResponseMessage;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.user.UserRepository;
import org.community.util.jwtutil.JwtUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
                parameter.getParameterType().equals(UserEntity.class);
    }

    // 헤더의 accessToken으로 userId를 조회하여 UserEntity를 리턴
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromJwt(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
    }
}
