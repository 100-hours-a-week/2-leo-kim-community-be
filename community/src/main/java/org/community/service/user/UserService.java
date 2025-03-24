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
@Transactional
public interface UserService {
    ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto, String imagePath);
    ResponseEntity<ApiResponse> login(UserLoginRequest userLoginRequestDto);
    ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, UserUpdateRequest userUpdateRequestDto, MultipartFile profileImage);
    ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, UserPasswordRequest userPasswordRequestDto);
    ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request);
    ResponseEntity<ApiResponse> getMe(HttpServletRequest request);
    ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, String nickname);
}
