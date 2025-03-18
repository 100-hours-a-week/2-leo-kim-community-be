package org.community.dto.user.request;

import org.community.entity.user.UserEntity;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    // Dto를 Entity로 변환시켜주는 메서드
    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profilePic(profileImage)
                .build();
    }
}
