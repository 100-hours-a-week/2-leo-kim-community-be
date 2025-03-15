package org.community.service.user;

import org.community.common.user.UserResponseMessage;
import org.community.dto.user.request.UserLoginRequest;
import org.community.dto.user.request.UserSignupRequest;
import org.community.dto.user.request.UserUpdateRequest;
import org.community.dto.user.response.ApiResponse;
import org.community.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.community.respository.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto) {
        Optional<UserEntity> findUserByEmail = userRepository.findByEmail(userSignupDto.getEmail());

        // 이메일 중복 체크
        // 이메일, 비밀번호 유효성 검사는 FE에서 맡아서 관리하는게 성능적으로 좋을것같습니다.
        if(findUserByEmail.isPresent())
            return ApiResponse.response(UserResponseMessage.DUPLICATE_EMAIL);

        // 회원가입 db 저장
        UserEntity savedUser = userRepository.save(userSignupDto.toEntity());
        // userId를 Map으로 만들어서 반환
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", savedUser.getUserId());

        // TODO : 패스워드 암호화
        return ApiResponse.response(UserResponseMessage.SIGNUP_SUCCESS, responseData);
    }

    public ResponseEntity<ApiResponse> login(UserLoginRequest userLoginRequestDto) {
        Optional<UserEntity> findUserByEmail = userRepository.findByEmail(userLoginRequestDto.getEmail());

        // 존재하지 않는 이메일
        if(findUserByEmail.isEmpty()){
            return ApiResponse.response(UserResponseMessage.INVALID_EMAIL);
        }

        // 패스워드 틀림
        UserEntity user = findUserByEmail.get();
        if(!user.getPassword().equals(userLoginRequestDto.getPassword())){
            return ApiResponse.response(UserResponseMessage.INVALID_PASSWORD);
        }

        // TODO : JWT TOKEN
        return ApiResponse.response(UserResponseMessage.LOGIN_SUCCESS, "accesstoken, refreshtoken");
    }

    public ResponseEntity<ApiResponse> updateUser(UserUpdateRequest userUpdateRequestDto) {

    }

}
