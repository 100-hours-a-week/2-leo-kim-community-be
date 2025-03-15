package common.user;

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
    INVALID_EMAIL_OR_PASSWORD(400, "INVALID_EMAIL_OR_PASSWORD"), // 로그인 실패

    // 회원 정보 수정 관련
    UPDATE_SUCCESS(204, "UPDATE_SUCCESS"),
    INVALID_NICKNAME_FORMAT(400, "INVALID_NICKNAME_FORMAT"); // 닉네임 형식 오류

    private final int statusCode;
    private final String message;
}
