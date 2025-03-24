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
import org.community.service.file.FileUploadService;
import org.community.util.jwtutil.JwtUtil;
import org.community.util.jwtutil.TokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.community.respository.user.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService fileUploadService;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto, String imagePath) {
        // 이메일 중복 체크
        // 이메일, 비밀번호 유효성 검사는 FE에서 맡아서 관리하는게 성능적으로 좋을것같습니다.
        Optional<UserEntity> userByEmail = userRepository.findByEmail(userSignupDto.getEmail());
        if (userByEmail.isPresent()) {
            throw new CustomException(UserResponseMessage.DUPLICATE_EMAIL);
        }
        Optional<UserEntity> userByNickname = userRepository.findByNickname(userSignupDto.getNickname());
        if (userByNickname.isPresent()) {
            throw new CustomException(UserResponseMessage.DUPLICATE_NICKNAME);
        }

        // 회원가입 db 저장
        userSignupDto.setPassword(bCryptPasswordEncoder.encode(userSignupDto.getPassword()));
        UserEntity savedUser = userSignupDto.toEntity();
        savedUser.setProfileImagePath(imagePath);
        userRepository.save(savedUser);

        return ApiResponse.response(UserResponseMessage.SIGNUP_SUCCESS);
    }


    public ResponseEntity<ApiResponse> login(UserLoginRequest userLoginRequestDto) {
        UserEntity user = getUserByEmail(userLoginRequestDto.getEmail());

        // 비밀번호 틀림
        if (!bCryptPasswordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            return ApiResponse.response(UserResponseMessage.INVALID_PASSWORD);
        }

        // 헤더에 추가
        TokenInfo jwt = jwtUtil.createToken(user.getEmail(), user.getUserId());
        HttpHeaders headers = new HttpHeaders();
        addHeaders(headers, jwt);

        // TODO : Redis에 refreshToken 추가 & 검증 및 재발급로직

        return ApiResponse.responseWithHeader(UserResponseMessage.LOGIN_SUCCESS, headers);
    }



    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, UserUpdateRequest userUpdateRequestDto, MultipartFile profileImage) {
        UserEntity user = getUserByToken(request);

        // 새 이미지가 없다면 그대로
        String imagePath = user.getProfileImagePath();
        // 있다면
        if (profileImage != null && !profileImage.isEmpty()) {
            // 업로드된 이미지 저장
            imagePath = fileUploadService.saveImage(profileImage, true);

            // 기존 이미지 파일 삭제
            File oldFile = getProfileImage(user);
            fileUploadService.deleteImage(oldFile);
        }

        // 닉네임, 프로필 사진 수정
        user.setNickname(userUpdateRequestDto.getNickname());
        user.setProfileImagePath(imagePath);
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }


    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, UserPasswordRequest userPasswordRequestDto) {
        UserEntity user = getUserByToken(request);

        // 새 비밀번호 수정
        String encodedPassword = bCryptPasswordEncoder.encode(userPasswordRequestDto.getNewPassword());
        user.setPassword(encodedPassword);
        return ApiResponse.response(UserResponseMessage.UPDATE_SUCCESS);
    }

    public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request) {
        UserEntity user = getUserByToken(request);
        List<PostEntity> postEntities = postRepository.findAllByUser(user);

        // user에 딸린 post들은 cascade로 db에선 자동으로 지워주지만,
        // 파일들은 손수 지워주자.
        postEntities.forEach((postEntity -> {
            File oldFile = getPostImage(postEntity);
            fileUploadService.deleteImage(oldFile);
        }));

        // user 프로필 이미지 삭제
        File oldFile = getProfileImage(user);
        fileUploadService.deleteImage(oldFile);

        // user db 삭제
        userRepository.delete(user);
        return ApiResponse.response(UserResponseMessage.DELETE_SUCCESS);
    }

    /* 현재 안쓰는 서비스로직
    public ResponseEntity<ApiResponse> getUser(Long userId) {
        UserEntity userInfo = getUserById(userId);
        UserResponse responseBody = UserResponse.builder()
                .nickname(userInfo.getNickname())
                .profileImage(userInfo.getProfileImagePath())
                .build();

        return ApiResponse.response(UserResponseMessage.USER_FETCH_SUCCESS, responseBody);
    }
     */

    public ResponseEntity<ApiResponse> getMe(HttpServletRequest request) {
        UserEntity myInfo = getUserByToken(request);
        UserResponse responseBody = UserResponse.builder()
                .email(myInfo.getEmail())
                .nickname(myInfo.getNickname())
                .profileImage(myInfo.getProfileImagePath())
                .build();

        return ApiResponse.response(UserResponseMessage.USER_FETCH_SUCCESS, responseBody);
    }

    public ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, String nickname) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        Optional<UserEntity> user = userRepository.findByNickname(nickname);

        // 닉네임에 해당하는 유저가 없으면 중복 x
        if (user.isEmpty())
            return ApiResponse.response(UserResponseMessage.NOT_DUPLICATE_NICKNAME);

        // 닉네임에 해당하는 유저가 본인이면 중복 x
        // 회원정보수정창에서 닉네임 바꾸지 않고 수정완료 눌렀을 때 처리
        if (user.get().getUserId().equals(userId))
            return ApiResponse.response(UserResponseMessage.NOT_DUPLICATE_NICKNAME);


        // 아니면 중복
        return ApiResponse.response(UserResponseMessage.DUPLICATE_NICKNAME);
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserResponseMessage.INVALID_EMAIL));
    }

    private UserEntity getUserByToken(HttpServletRequest request){
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
    }


    private void addHeaders(HttpHeaders headers, TokenInfo jwt) {
        headers.add("Authorization", "Bearer " + jwt.getAccessToken());
        headers.add("refreshToken", jwt.getRefreshToken());
    }

    private File getProfileImage(UserEntity user) {
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + user.getProfileImagePath();
        return new File(fullOldImagePath);
    }

    private File getPostImage(PostEntity postEntity) {
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + postEntity.getPostImagePath();
        return new File(fullOldImagePath);
    }
}
