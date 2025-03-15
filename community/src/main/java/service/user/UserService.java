package service.user;

import common.user.UserResponseMessage;
import dto.user.request.UserSignupRequest;
import dto.user.response.ApiResponse;
import entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import respository.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(userSignupDto.getEmail());

        // 이메일 중복 체크
        // 이메일, 비밀번호 유효성 검사는 FE에서 맡아서 관리하는게 성능적으로 좋을것같습니다.
        if(existingUser.isPresent())
            return ApiResponse.response(UserResponseMessage.DUPLICATE_EMAIL);

        // 회원가입 db 저장
        UserEntity savedUser = userRepository.save(userSignupDto.toEntity());
        // userId를 Map으로 만들어서 반환
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", savedUser.getUserId());

        return ApiResponse.response(UserResponseMessage.SIGNUP_SUCCESS, responseData);
    }
}
