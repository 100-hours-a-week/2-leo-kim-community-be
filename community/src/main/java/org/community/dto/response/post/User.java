package org.community.dto.response.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String nickname;
    private String profileImagePath;

    @Builder
    User(String nickname, String profileImagePath){
        this.nickname = nickname;
        this.profileImagePath = profileImagePath;
    }
}
