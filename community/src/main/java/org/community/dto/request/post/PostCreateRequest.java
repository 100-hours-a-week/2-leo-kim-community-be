package org.community.dto.request.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostCreateRequest {
    private String title;
    private String contents;
    private String image;

    public PostEntity toEntity(UserEntity user){
        return PostEntity.builder()
                .user(user)
                .title(title)
                .contents(contents)
                .image(image)
                .build();
    }
}
