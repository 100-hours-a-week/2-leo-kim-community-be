package dto.user.request;

import entity.user.UserEntity;
import lombok.*;

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
