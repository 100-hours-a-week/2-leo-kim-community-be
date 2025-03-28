package org.community.service.user;

import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.Token;
import org.community.common.function.CommonFunctions;
import org.community.common.user.UserResponseMessage;
import org.community.dto.request.user.UserLoginRequest;
import org.community.dto.request.user.UserSignupRequest;
import org.community.dto.request.user.UserUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.user.UserRepository;
import org.community.service.file.FileUploadService;
import org.community.util.jwtutil.TokenInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 테스트")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private CommonFunctions commonFunctions;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("유저 업데이트")
    void 유저_업데이트() {
        // given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("new_nickname");

        MultipartFile mockImage = new MockMultipartFile("new_image","new.png","image/png", "fake-image-content".getBytes());
        File oldImage = mock(File.class);

        UserEntity updateUser = UserEntity.builder()
                .email("old_id@naver.com")
                .password("old_password")
                .nickname("old_nickname")
                .profileImagePath("old_path")
                .build();

        String newImagePath = "/upload/profiles/fake.png";

        // stubbing
        given(commonFunctions.getUserByToken(httpServletRequest))
                .willReturn(updateUser);

        given(commonFunctions.getProfileImage(updateUser))
                .willReturn(oldImage);

        willDoNothing().given(fileUploadService).deleteImage(oldImage);


        given(fileUploadService.saveImage(any(MultipartFile.class),eq(true)))
                .willReturn(newImagePath);

        //when
        ResponseEntity<ApiResponse> response = userService.updateUser(httpServletRequest, request, mockImage);

        //then
        assertEquals(UserResponseMessage.UPDATE_SUCCESS.getMessage(), response.getBody().getMessage());
        assertEquals("new_nickname", updateUser.getNickname());
        assertEquals(newImagePath, updateUser.getProfileImagePath());
    }

    @Test
    @DisplayName("로그인 실패(비밀번호)")
    void 로그인_실패_비밀번호(){
        //given
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("correct_email@naver.com");
        request.setPassword("wrong_password");

        UserEntity user = UserEntity.builder()
                .email("correct_email@naver.com")
                .password("encoded_correct_password")
                .build();

        //stubbing
        given(commonFunctions.getUserByEmail("correct_email@naver.com")).willReturn(user);
        given(commonFunctions.isSamePassword("wrong_password","encoded_correct_password"))
                .willReturn(false);

        //when
        ResponseEntity<ApiResponse> response = userService.login(request);

        //then
        assertEquals(UserResponseMessage.INVALID_PASSWORD.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("로그인 실패(이메일)")
    void 로그인_실패_이메일(){
        //given
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("wrong_email@example.com");
        request.setPassword("1234");

        //stubbing
        given(commonFunctions.getUserByEmail("wrong_email@example.com"))
                .willThrow(new CustomException(UserResponseMessage.USER_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.login(request);
        });

        assertEquals(UserResponseMessage.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        // given
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("1234");

        UserEntity mockUser = UserEntity.builder()
                .email("test@example.com")
                .password("encoded_password")
                .build();
        mockUser.setUserId(1L);

        TokenInfo tokenInfo = TokenInfo.builder()
                .grantType("Bearer")
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        //stubbing
        given(commonFunctions.getUserByEmail("test@example.com")).willReturn(mockUser);
        given(commonFunctions.isSamePassword("1234","encoded_password")).willReturn(true);
        given(commonFunctions.createJwt("test@example.com",1L)).willReturn(tokenInfo);
        willAnswer(invocation -> {
            HttpHeaders headers = invocation.getArgument(0);
            TokenInfo token = invocation.getArgument(1);
            headers.add("Authorization", token.getGrantType() + " " + token.getAccessToken());
            return null;
        }).given(commonFunctions).addHeaders(any(HttpHeaders.class), eq(tokenInfo));

        //when
        ResponseEntity<ApiResponse> response = userService.login(request);

        assertEquals(UserResponseMessage.LOGIN_SUCCESS.getMessage(), response.getBody().getMessage());
        assertNotNull(response.getHeaders().get("Authorization"));
        assertEquals("Bearer access-token",response.getHeaders().getFirst("Authorization"));
    }


    @Test
    void 회원가입_성공() {
        // given
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("test@example.com");
        request.setNickname("tester");
        request.setPassword("1234");

        MultipartFile profileImage = mock(MultipartFile.class);

        // stubbing
        doNothing().when(commonFunctions).checkDuplicateEmail(anyString());
        doNothing().when(commonFunctions).checkDuplicateNickname(anyString());
        given(commonFunctions.getImagePath(any(), eq(true)))
                .willReturn("/upload/profiles/fake_uuid_test.png");
        given(commonFunctions.encodePassword(anyString())).willReturn("encodedPwd");
        given(userRepository.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseEntity<ApiResponse> response = userService.signup(request, profileImage);

        // then
        assertEquals(UserResponseMessage.SIGNUP_SUCCESS.getMessage(), response.getBody().getMessage());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void 이메일중복_예외() {
        // given
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("duplicate@example.com");

        // stubbing
        doThrow(new CustomException(UserResponseMessage.DUPLICATE_EMAIL))
                .when(commonFunctions).checkDuplicateEmail(anyString());

        // when & then
        assertThrows(CustomException.class, () -> userService.signup(request, null));
    }

    @Test
    void 닉네임중복_예외() {
        // given
        UserSignupRequest request = new UserSignupRequest();
        request.setEmail("unique@example.com");
        request.setNickname("duplicatedNick");

        doNothing().when(commonFunctions).checkDuplicateEmail(anyString());
        doThrow(new CustomException(UserResponseMessage.DUPLICATE_NICKNAME))
                .when(commonFunctions).checkDuplicateNickname(anyString());

        // when & then
        assertThrows(CustomException.class, () -> userService.signup(request, null));
    }
}
