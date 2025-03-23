package org.community.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.dto.request.user.*;
import org.community.dto.response.ApiResponse;
import org.community.dto.response.user.UserResponse;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.community.global.CustomException;
import org.community.respository.post.PostRepository;
import org.community.util.jwtutil.JwtUtil;
import org.community.util.jwtutil.TokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.community.respository.user.UserRepository;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto, String imagePath) {
        Optional<UserEntity> findUserByEmail = userRepository.findByEmail(userSignupDto.getEmail());
        Optional<UserEntity> findUserByNickname = userRepository.findByNickname(userSignupDto.getNickname());

        // 이메일 중복 체크
        // 이메일, 비밀번호 유효성 검사는 FE에서 맡아서 관리하는게 성능적으로 좋을것같습니다.
        if (findUserByEmail.isPresent())
            return ApiResponse.response(UserResponseMessage.DUPLICATE_EMAIL);

        if (findUserByNickname.isPresent())
            return ApiResponse.response(UserResponseMessage.DUPLICATE_NICKNAME);

        // 회원가입 db 저장
        userSignupDto.setPassword(bCryptPasswordEncoder.encode(userSignupDto.getPassword()));
        UserEntity savedUser = userSignupDto.toEntity();
        savedUser.setProfilePic(imagePath);
        userRepository.save(savedUser);

        // userId를 Map으로 만들어서 반환
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", savedUser.getUserId());

        return ApiResponse.response(UserResponseMessage.SIGNUP_SUCCESS, responseData);
    }

    public ResponseEntity<ApiResponse> login(UserLoginRequest userLoginRequestDto) {
        Optional<UserEntity> findUserByEmail = userRepository.findByEmail(userLoginRequestDto.getEmail());

        // 존재하지 않는 이메일
        if (findUserByEmail.isEmpty()) {
            return ApiResponse.response(UserResponseMessage.INVALID_EMAIL);
        }

        // 패스워드 틀림
        UserEntity user = findUserByEmail.get();
        if (!bCryptPasswordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            return ApiResponse.response(UserResponseMessage.INVALID_PASSWORD);
        }

        TokenInfo jwt = jwtUtil.createToken(findUserByEmail.get().getEmail(), findUserByEmail.get().getUserId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt.getAccessToken());
        headers.add("refreshToken", jwt.getRefreshToken());

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("profileImage", user.getProfilePic());

        return ApiResponse.responseWithHeader(UserResponseMessage.LOGIN_SUCCESS, responseBody, headers);
    }

    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, UserUpdateRequest userUpdateRequestDto, String imagePath) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(UserResponseMessage.USER_NOT_FOUND)
        );

        // 기존 이미지 파일 삭제
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + user.getProfilePic();
        File oldFile = new File(fullOldImagePath);

        if (oldFile.exists()) {
            boolean deleted = oldFile.delete();
            if (!deleted) {
                log.warn("기존 이미지 파일 삭제 실패: {}", fullOldImagePath);
            } else {
                log.info("기존 이미지 파일 삭제 성공: {}", fullOldImagePath);
            }
        }

        user.setNickname(userUpdateRequestDto.getNickname());
        user.setProfilePic(imagePath);
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }

    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, UserPasswordRequest userPasswordRequestDto) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(UserResponseMessage.USER_NOT_FOUND)
        );
        String encodedPassword = bCryptPasswordEncoder.encode(userPasswordRequestDto.getNewPassword());
        user.setPassword(encodedPassword);
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }

    public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));

        List<PostEntity> postEntitiesByUser = postRepository.findAllByUser(user);

        // user에 딸린 post들은 cascade로 db에선 자동으로 지워주지만,
        // 파일들은 손수 지워주자.
        postEntitiesByUser.forEach((postEntity -> {
            String imagePath = postEntity.getImage();
            if(imagePath != null) {
                String fullOldImagePath = System.getProperty("user.dir") + "/community" + postEntity.getImage();
                File oldFile = new File(fullOldImagePath);

                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    if (!deleted) {
                        log.warn("기존 이미지 파일 삭제 실패: {}", fullOldImagePath);
                    } else {
                        log.info("기존 이미지 파일 삭제 성공: {}", fullOldImagePath);
                    }
                }
            }
        }));

        // 기존 이미지 파일 삭제
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + user.getProfilePic();
        File oldFile = new File(fullOldImagePath);

        if (oldFile.exists()) {
            boolean deleted = oldFile.delete();
            if (!deleted) {
                log.warn("기존 이미지 파일 삭제 실패: {}", fullOldImagePath);
            } else {
                log.info("기존 이미지 파일 삭제 성공: {}", fullOldImagePath);
            }
        }

        userRepository.delete(user);
        return ApiResponse.response(UserResponseMessage.DELETE_SUCCESS);
    }

    public ResponseEntity<ApiResponse> getUser(Long userId) {
        UserEntity userInfo = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
        UserResponse responseBody = UserResponse.builder()
                .nickname(userInfo.getNickname())
                .profileImage(userInfo.getProfilePic())
                .build();

        return ApiResponse.response(UserResponseMessage.USER_FETCH_SUCCESS, responseBody);
    }

    public ResponseEntity<ApiResponse> getMe(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity myInfo = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
        UserResponse responseBody = UserResponse.builder()
                .email(myInfo.getEmail())
                .nickname(myInfo.getNickname())
                .profileImage(myInfo.getProfilePic())
                .build();

        return ApiResponse.response(UserResponseMessage.USER_FETCH_SUCCESS, responseBody);
    }

    public ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, String nickname) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        Optional<UserEntity> user = userRepository.findByNickname(nickname);
        if (user.isEmpty())
            return ApiResponse.response(UserResponseMessage.NOT_DUPLICATE_NICKNAME);

        if (user.get().getUserId().equals(userId))
            return ApiResponse.response(UserResponseMessage.NOT_DUPLICATE_NICKNAME);

        return ApiResponse.response(UserResponseMessage.DUPLICATE_NICKNAME);
    }
}
