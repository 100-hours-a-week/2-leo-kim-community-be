package org.community.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.community.dto.request.user.*;
import org.community.dto.response.ApiResponse;
import org.community.entity.user.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@Transactional
public interface UserService {
    ResponseEntity<ApiResponse> signup(UserSignupRequest userSignupDto, String imagePath);
    ResponseEntity<ApiResponse> login(UserLoginRequest userLoginRequestDto);
    ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, UserUpdateRequest userUpdateRequestDto, MultipartFile profileImage);
    ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, UserPasswordRequest userPasswordRequestDto);
    ResponseEntity<ApiResponse> deleteUser(UserEntity user);
    ResponseEntity<ApiResponse> getMe(UserEntity user);
    ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, String nickname);
}
