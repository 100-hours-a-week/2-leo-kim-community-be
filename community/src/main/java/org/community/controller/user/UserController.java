package org.community.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import org.community.dto.request.user.UserLoginRequest;
import org.community.dto.request.user.UserPasswordRequest;
import org.community.dto.request.user.UserSignupRequest;
import org.community.dto.request.user.UserUpdateRequest;
import org.community.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.community.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserSignupRequest userSignupDto){
        return userService.signup(userSignupDto);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest userLoginRequestDto){
        return userService.login(userLoginRequestDto);
    }

    // TODO : jwt util을 만들어 사용자 정보 인식 및 유효성 검증
    @PutMapping
    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, @RequestBody UserUpdateRequest userUpdateRequestDto){
        return userService.updateUser(request, userUpdateRequestDto);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, @RequestBody UserPasswordRequest userPasswordRequestDto){
        return userService.updateUserPassword(request,userPasswordRequestDto);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request){
        return userService.deleteUser(request);
    }
}
