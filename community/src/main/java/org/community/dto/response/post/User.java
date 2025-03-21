package org.community.dto.response.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String nickname;
    private String profileImage;

    @Builder
    User(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
