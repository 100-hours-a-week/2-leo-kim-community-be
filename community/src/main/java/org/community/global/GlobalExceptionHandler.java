package org.community.global;

import io.jsonwebtoken.JwtException;
import org.community.common.user.UserResponseMessage;
import org.community.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException e) {
        UserResponseMessage responseMessage = UserResponseMessage.JWT_INVALID; // 기본값
        if (e.getMessage().contains("expired")) {
            responseMessage = UserResponseMessage.JWT_EXPIRED;
        }
        return ApiResponse.response(responseMessage);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomJwtException(CustomException e) {
        return ApiResponse.response(e.getResponseMessage());
    }
}
