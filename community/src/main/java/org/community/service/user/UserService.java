package org.community.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.dto.user.request.UserLoginRequest;
import org.community.dto.user.request.UserPasswordRequest;
import org.community.dto.user.request.UserSignupRequest;
import org.community.dto.user.request.UserUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.community.global.CustomJwtException;
import org.community.util.jwtutil.JwtUtil;
import org.community.util.jwtutil.TokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.community.respository.user.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto) {
        Optional<UserEntity> findUserByEmail = userRepository.findByEmail(userSignupDto.getEmail());

        // 이메일 중복 체크
        // 이메일, 비밀번호 유효성 검사는 FE에서 맡아서 관리하는게 성능적으로 좋을것같습니다.
        if(findUserByEmail.isPresent())
            return ApiResponse.response(UserResponseMessage.DUPLICATE_EMAIL);

        // 회원가입 db 저장
        userSignupDto.setPassword(bCryptPasswordEncoder.encode(userSignupDto.getPassword()));
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
        if(!bCryptPasswordEncoder.matches(userLoginRequestDto.getPassword(),user.getPassword())){
            return ApiResponse.response(UserResponseMessage.INVALID_PASSWORD);
        }

        TokenInfo jwt = jwtUtil.createToken(findUserByEmail.get().getEmail(), findUserByEmail.get().getUserId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt.getAccessToken());
        headers.add("refreshToken", jwt.getRefreshToken());

        // TODO : JWT TOKEN
        return ApiResponse.responseWithHeader(UserResponseMessage.LOGIN_SUCCESS,headers);
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, UserUpdateRequest userUpdateRequestDto) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
            new CustomJwtException(UserResponseMessage.JWT_INVALID)
        );
        user.setNickname(userUpdateRequestDto.getNickname());
        user.setProfilePic(userUpdateRequestDto.getProfileImage());
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, UserPasswordRequest userPasswordRequestDto) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new CustomJwtException(UserResponseMessage.JWT_INVALID)
        );
        String encodedPassword = bCryptPasswordEncoder.encode(userPasswordRequestDto.getNewPassword());
        user.setPassword(encodedPassword);
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }

    @Transactional
    public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        userRepository.deleteById(userId);
        return ApiResponse.response(UserResponseMessage.DELETE_SUCCESS);
    }
}
