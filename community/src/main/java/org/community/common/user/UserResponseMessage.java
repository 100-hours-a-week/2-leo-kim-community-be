package org.community.common.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserResponseMessage {
    // 회원가입 관련
    SIGNUP_SUCCESS(201, "SIGNUP_SUCCESS"),
    INVALID_INFO(400, "INVALID_INFO"), // 이메일, 닉네임, 패스워드 형식 오류
    DUPLICATE_EMAIL(409, "DUPLICATE_EMAIL"), // 이메일 중복

    // 로그인 관련
    LOGIN_SUCCESS(200, "LOGIN_SUCCESS"),
    INVALID_EMAIL(400, "INVALID_EMAIL"), // 로그인 실패
    INVALID_PASSWORD(400, "INVALID_PASSWORD"),

    // 회원 정보 수정 관련
    UPDATE_SUCCESS(204, "UPDATE_SUCCESS"),
    INVALID_NICKNAME_FORMAT(400, "INVALID_NICKNAME_FORMAT"), // 닉네임 형식 오류

    // 회원 삭제 관련
    DELETE_SUCCESS(204, "DELETE_SUCCESS" ),

    //JWT 관련
    JWT_CREATION_SUCCESS(200, "JWT_CREATION_SUCCESS"), // JWT 생성 성공
    JWT_EXPIRED(401, "JWT_EXPIRED"), // JWT 만료됨
    JWT_INVALID(401, "JWT_INVALID"), // JWT가 유효하지 않음
    JWT_UNSUPPORTED(401, "JWT_UNSUPPORTED"), // 지원하지 않는 JWT 형식
    JWT_MISSING(401, "JWT_MISSING"), // 요청 헤더에 JWT 없음
    JWT_VERIFICATION_FAILED(403, "JWT_VERIFICATION_FAILED"); // JWT 검증 실패




    private final int statusCode;
    private final String message;
}
