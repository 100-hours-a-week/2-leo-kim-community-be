package org.community.dto.response.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String nickname;
    private String profileImage;

    @Builder
    UserResponse(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
