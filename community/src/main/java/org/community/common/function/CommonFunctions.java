package org.community.common.function;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.community.common.user.UserResponseMessage;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.user.UserRepository;
import org.community.service.file.FileUploadService;
import org.community.util.jwtutil.JwtUtil;
import org.community.util.jwtutil.TokenInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommonFunctions {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService fileUploadService;
    private final JwtUtil jwtUtil;

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserResponseMessage.INVALID_EMAIL));
    }

    public UserEntity getUserByToken(HttpServletRequest request){
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
    }


    public void addHeaders(HttpHeaders headers, TokenInfo jwt) {
        headers.add("Authorization", "Bearer " + jwt.getAccessToken());
        headers.add("refreshToken", jwt.getRefreshToken());
    }

    public File getProfileImage(UserEntity user) {
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + user.getProfileImagePath();
        return new File(fullOldImagePath);
    }

    public File getPostImage(PostEntity postEntity) {
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + postEntity.getPostImagePath();
        return new File(fullOldImagePath);
    }

    public Long getUserIdFromJwt(HttpServletRequest request){
        return jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
    }

    public TokenInfo createJwt(String email, Long id){
        return jwtUtil.createToken(email,id);
    }

    public String encodePassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean isSamePassword(String rawPassword, String encodedPassword){
        return bCryptPasswordEncoder.matches(rawPassword,encodedPassword);
    }

    public void checkDuplicateEmail(String email){
        Optional<UserEntity> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new CustomException(UserResponseMessage.DUPLICATE_EMAIL);
        }
    }

    public void checkDuplicateNickname(String nickname){
        Optional<UserEntity> userByNickname = userRepository.findByNickname(nickname);
        if (userByNickname.isPresent()) {
            throw new CustomException(UserResponseMessage.DUPLICATE_NICKNAME);
        }
    }


    public String getImagePath(MultipartFile profileImage, boolean isProfileImage) {
        String imagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imagePath = fileUploadService.saveImage(profileImage, true);
        }
        return imagePath;
    }
}
