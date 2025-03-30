package org.community.service.user;

import org.community.common.function.CommonFunctions;
import org.community.common.user.UserResponseMessage;
import org.community.dto.request.user.UserLoginRequest;
import org.community.dto.request.user.UserPasswordRequest;
import org.community.dto.request.user.UserSignupRequest;
import org.community.dto.request.user.UserUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.dto.response.user.UserResponse;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.post.PostRepository;
import org.community.respository.user.UserRepository;
import org.community.service.file.FileUploadService;
import org.community.util.jwtutil.TokenInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 테스트")
class UserServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private CommonFunctions commonFunctions;

    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("중복 닉네임 체크")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"new_nickname, null", "old_nickname, me", "duplicate_nickname, other"})
    void 중복_닉네임_체크(String nickname, String userType) {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        UserEntity me = UserEntity.builder()
                .nickname("old_nickname")
                .build();
        me.setUserId(1L);

        UserEntity other = UserEntity.builder()
                .nickname("duplicate_nickname")
                .build();
        other.setUserId(2L);

        // 닉네임에 해당하는 사용자 결정
        UserEntity foundUser = switch (userType) {
            case "me" -> me;
            case "other" -> other;
            default -> null;
        };

        String expectedMessage = userType.equals("other") ?
                UserResponseMessage.DUPLICATE_NICKNAME.getMessage() : UserResponseMessage.NOT_DUPLICATE_NICKNAME.getMessage();

        given(commonFunctions.getUserIdFromJwt(httpServletRequest))
                .willReturn(1L);
        given(userRepository.findByNickname(nickname))
                .willReturn(Optional.ofNullable(foundUser));

        ResponseEntity<ApiResponse> response = userService.isDuplicateNickname(httpServletRequest, nickname);

        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("내 정보 조회")
    void 내정보_조회() {
        UserEntity user = UserEntity.builder()
                .email("my_email")
                .nickname("my_nickname")
                .profileImagePath("my_image_path")
                .build();

        UserResponse expected = UserResponse.builder()
                .email("my_email")
                .nickname("my_nickname")
                .profileImage("my_image_path")
                .build();

        ResponseEntity<ApiResponse> response = userService.getMe(user);

        assertEquals(UserResponseMessage.USER_FETCH_SUCCESS.getMessage(), response.getBody().getMessage());
        assertEquals(expected, response.getBody().getData());

    }


    @Test
    @DisplayName("유저 삭제")
    void 유저_삭제() {
        UserEntity user = UserEntity.builder()
                .email("email")
                .profileImagePath("imagePath")
                .build();

        PostEntity post1 = mock(PostEntity.class);
        PostEntity post2 = mock(PostEntity.class);
        List<PostEntity> posts = List.of(post1, post2);

        File profileImageFile = mock(File.class);
        File postImageFile1 = mock(File.class);
        File postImageFile2 = mock(File.class);

        given(postRepository.findAllByUser(user))
                .willReturn(posts);
        given(commonFunctions.getPostImage(post1))
                .willReturn(postImageFile1);
        given(commonFunctions.getPostImage(post2))
                .willReturn(postImageFile2);
        given(commonFunctions.getProfileImage(user))
                .willReturn(profileImageFile);

        willDoNothing().given(fileUploadService).deleteImage(postImageFile1);
        willDoNothing().given(fileUploadService).deleteImage(postImageFile2);
        willDoNothing().given(fileUploadService).deleteImage(profileImageFile);
        willDoNothing().given(userRepository).delete(user);

        ResponseEntity<ApiResponse> response = userService.deleteUser(user);

        assertEquals(UserResponseMessage.DELETE_SUCCESS.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("유저 비밀번호 업데이트 성공")
    void 유저_비밀번호_업데이트_성공() {
        // given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        UserPasswordRequest request = new UserPasswordRequest();
        request.setNewPassword("new_password");
        UserEntity user = UserEntity.builder()
                .password("old_encoded_password")
                .build();

        //stubbing
        given(commonFunctions.encodePassword("new_password"))
                .willReturn("encoded_password");

        given(commonFunctions.getUserByToken(httpServletRequest))
                .willReturn(user);

        //when
        ResponseEntity<ApiResponse> response
                = userService.updateUserPassword(httpServletRequest, request);

        //then
        assertEquals(UserResponseMessage.UPDATE_SUCCESS.getMessage(), response.getBody().getMessage());
        assertEquals("encoded_password", user.getPassword());
    }

    @Test
    @DisplayName("유저 업데이트 성공")
    void 유저_업데이트_성공() {
        // given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("Authorization", "Bearer fake_token");
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("new_nickname");

        MultipartFile mockImage = new MockMultipartFile("new_image", "new.png", "image/png", "fake-image-content".getBytes());
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

        willDoNothing().given(fileUploadService)
                .deleteImage(oldImage);

        given(fileUploadService.saveImage(any(MultipartFile.class), eq(true)))
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
    void 로그인_실패_비밀번호() {
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
        given(commonFunctions.isSamePassword("wrong_password", "encoded_correct_password"))
                .willReturn(false);

        //when
        ResponseEntity<ApiResponse> response = userService.login(request);

        //then
        assertEquals(UserResponseMessage.INVALID_PASSWORD.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("로그인 실패(이메일)")
    void 로그인_실패_이메일() {
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

    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @DisplayName("로그인")
    @CsvSource({"correct_email, correct_password, LOGIN_SUCCESS", "wrong_email, any_password, WRONG_EMAIL", "correct_email, wrong_password, WRONG_PASSWORD"})
    void 로그인(String email, String password, String currentCase) {
        // given
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        UserEntity mockUser = UserEntity.builder()
                .email("correct_email")
                .password("correct_encoded_password")
                .build();
        mockUser.setUserId(1L);

        TokenInfo tokenInfo = TokenInfo.builder()
                .grantType("Bearer")
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        Map<String, String> messageMap = Map.of(
                "LOGIN_SUCCESS", UserResponseMessage.LOGIN_SUCCESS.getMessage(),
                "WRONG_PASSWORD", UserResponseMessage.INVALID_PASSWORD.getMessage(),
                "WRONG_EMAIL", UserResponseMessage.USER_NOT_FOUND.getMessage()
        );
        String expectedMessage = messageMap.get(currentCase);

        //stubbing
        switch (currentCase) {
            case "LOGIN_SUCCESS":
                given(commonFunctions.getUserByEmail("correct_email"))
                        .willReturn(mockUser);
                given(commonFunctions.isSamePassword("correct_password", "correct_encoded_password"))
                        .willReturn(true);
                given(commonFunctions.createJwt("correct_email", 1L))
                        .willReturn(tokenInfo);
                break;
            case "WRONG_EMAIL":
                given(commonFunctions.getUserByEmail("wrong_email"))
                        .willThrow(new CustomException(UserResponseMessage.USER_NOT_FOUND));
                break;
            case "WRONG_PASSWORD":
                given(commonFunctions.getUserByEmail("correct_email"))
                        .willReturn(mockUser);
                given(commonFunctions.isSamePassword("wrong_password", "correct_encoded_password"))
                        .willReturn(false);
                break;
        }

        if(currentCase.equals("LOGIN_SUCCESS")) {
            willAnswer(invocation -> {
                HttpHeaders headers = invocation.getArgument(0);
                TokenInfo token = invocation.getArgument(1);
                headers.add("Authorization", token.getGrantType() + " " + token.getAccessToken());
                return null;
            }).given(commonFunctions).addHeaders(any(HttpHeaders.class), eq(tokenInfo));
        }

        if(currentCase.equals("WRONG_EMAIL")){
            CustomException exception = assertThrows(CustomException.class, () -> {
                userService.login(request);
            });
            assertEquals(expectedMessage, exception.getMessage());
        }
        else{
            ResponseEntity<ApiResponse> response = userService.login(request);
            assertEquals(expectedMessage, response.getBody().getMessage());

            if (expectedMessage.equals(UserResponseMessage.LOGIN_SUCCESS.getMessage())) {
                assertNotNull(response.getHeaders().get("Authorization"));
                assertEquals("Bearer access-token", response.getHeaders().getFirst("Authorization"));
            }
        }

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
