package org.community.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.community.dto.request.user.*;
import org.community.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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
    private final FileUploadService fileUploadService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMe(HttpServletRequest request){
        log.info("**************{}",System.getProperty("user.dir"));
        return userService.getMe(request);
    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> signup(@RequestPart("data") UserSignupRequest request,
                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileImage){

        String imagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imagePath = fileUploadService.saveProfileImage(profileImage);
        }
        return userService.signup(request, imagePath);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest userLoginRequestDto){
        return userService.login(userLoginRequestDto);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateUser(HttpServletRequest request, @RequestPart("data") UserUpdateRequest userUpdateRequestDto, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage){

        String imagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imagePath = fileUploadService.saveProfileImage(profileImage);
        }
        log.info("********* imagePath{}", imagePath);

        return userService.updateUser(request,  userUpdateRequestDto, imagePath);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updateUserPassword(HttpServletRequest request, @RequestBody UserPasswordRequest userPasswordRequestDto){
        return userService.updateUserPassword(request,userPasswordRequestDto);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(HttpServletRequest request){
        return userService.deleteUser(request);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, @PathVariable String nickname){
        log.info(nickname);
        return userService.isDuplicateNickname(request,nickname);
    }
}
