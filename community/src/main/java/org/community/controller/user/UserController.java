package org.community.controller.user;

import org.community.dto.user.request.UserLoginRequest;
import org.community.dto.user.request.UserSignupRequest;
import org.community.dto.user.response.ApiResponse;
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
}
