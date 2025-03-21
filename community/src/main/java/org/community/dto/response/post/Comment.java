package org.community.dto.response.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.community.entity.comment.CommentEntity;
import org.community.entity.post.PostEntity;

import java.util.Objects;

@Getter
public class Comment {
    private final Long id;
    private final String nickname;
    private final String contents;
    private final String regDate;
    private final Boolean isMyComment;

    public Comment(Long userId, CommentEntity commentEntity){
        this.id = commentEntity.getCommentId();
        this.nickname = commentEntity.getUser().getNickname();
        this.contents = commentEntity.getContents();
        this.regDate = commentEntity.getCommentRegDate().toString();
        this.isMyComment = Objects.equals(userId,commentEntity.getUser().getUserId());
    }
}
