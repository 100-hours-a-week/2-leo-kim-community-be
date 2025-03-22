package org.community.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.community.dto.request.user.*;
import org.community.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.community.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMe(HttpServletRequest request){
        return userService.getMe(request);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserSignupRequest userSignupDto){
        return userService.signup(userSignupDto);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest userLoginRequestDto){
        return userService.login(userLoginRequestDto);
    }

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

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ApiResponse> isDuplicateNickname(HttpServletRequest request, @PathVariable String nickname){
        log.info(nickname);
        return userService.isDuplicateNickname(request,nickname);
    }
}
