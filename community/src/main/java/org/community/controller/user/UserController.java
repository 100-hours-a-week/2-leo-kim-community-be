package org.community.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.community.annotation.CurrentUser;
import org.community.dto.request.user.*;
import org.community.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.community.entity.user.UserEntity;
import org.community.service.file.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.community.service.user.UserService;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* 현재 안쓰는 endpoint
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }
     */

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> signup(@RequestPart("data") UserSignupRequest request,
                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileImage){

        return userService.signup(request, profileImage);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest userLoginRequestDto){
        return userService.login(userLoginRequestDto);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, @RequestPart("data") UserUpdateRequest userUpdateRequestDto, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage){
        return userService.updateUser(request, userUpdateRequestDto, profileImage);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, @RequestBody UserPasswordRequest userPasswordRequestDto){
        return userService.updateUserPassword(request,userPasswordRequestDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMe(@CurrentUser UserEntity user){
        return userService.getMe(user);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(@CurrentUser UserEntity user){
        return userService.deleteUser(user);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, @PathVariable String nickname){
        return userService.isDuplicateNickname(request,nickname);
    }
}
