package org.community.dto.response.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.community.entity.comment.CommentEntity;
import org.community.entity.post.PostEntity;

import java.util.Objects;

@Getter
public class Comment {
    private final String commentAuthorNickname;
    private final String commentContents;
    private final String commentRegDate;
    private final Boolean isMyComment;

    public Comment(Long userId, CommentEntity commentEntity){
        this.commentAuthorNickname = commentEntity.getUser().getNickname();
        this.commentContents = commentEntity.getContents();
        this.commentRegDate = commentEntity.getCommentRegDate().toString();
        this.isMyComment = Objects.equals(userId,commentEntity.getUser().getUserId());
    }
}
