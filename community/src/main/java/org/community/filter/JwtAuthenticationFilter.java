package org.community.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.community.global.CustomException;
import org.community.util.jwtutil.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            try {
                if (jwtUtil.validateToken(jwt)) {
                    // 🔹 JWT에서 이메일 & 인증 객체 생성
                    Authentication authentication = jwtUtil.getAuthentication(jwt);

                    // 🔹 SecurityContextHolder에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
            // TODO : AuthenticationEntryPoint를 사용하는 방법 모색
            catch (CustomException e){
                // 🔥 직접 JSON 응답 작성
                response.setStatus(e.getResponseMessage().getStatusCode());
                response.setContentType("application/json;charset=UTF-8");

                String jsonResponse = String.format(
                        "{\"message\": \"%s\", \"data\": null}",
                        e.getResponseMessage().name()
                );

                response.getWriter().write(jsonResponse);
                response.getWriter().flush();
                return; // 필터 체인 종료
            }
        }

        filterChain.doFilter(request, response);
    }
}
