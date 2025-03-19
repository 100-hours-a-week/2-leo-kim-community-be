package org.community.dto.request.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.community.entity.comment.CommentEntity;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequest {
    private String contents;

    public CommentEntity toEntity(UserEntity user, PostEntity post){
        return CommentEntity.builder()
                .user(user)
                .post(post)
                .contents(contents)
                .build();
    }
}
