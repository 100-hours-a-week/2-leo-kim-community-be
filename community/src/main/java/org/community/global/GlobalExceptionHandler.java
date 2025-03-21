package org.community.global;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException e) {
        UserResponseMessage responseMessage = UserResponseMessage.JWT_INVALID; // 기본값
        if (e.getMessage().contains("expired")) {
            responseMessage = UserResponseMessage.JWT_EXPIRED;
        }
        return ApiResponse.response(responseMessage);
    }
}
