package org.community.dto.request.user;

import org.community.entity.user.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserSignupRequest {
    private String email;
    private String password;
    private String nickname;

    // Dto를 Entity로 변환시켜주는 메서드
    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
