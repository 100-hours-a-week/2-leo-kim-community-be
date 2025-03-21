package org.community.dto.response.post;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.community.entity.post.PostEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PostDetailResponse {
    private final Long authorUserId;
    private final String title;
    private final String contents;
    private final String image;
    private final Integer views;
    private final Integer likes;
    private final Integer comments;
    private final Date regDate;
    private final Boolean isMyPost;
    private Boolean isLiked;
    private List<Comment> commentList;

    public PostDetailResponse(PostEntity postEntity, Long userId){
        this.authorUserId = postEntity.getUser().getUserId();
        this.title = postEntity.getTitle();
        this.contents = postEntity.getContents();
        this.image = postEntity.getImage();
        this.views = postEntity.getViews();
        this.likes = postEntity.getLikesCnt();
        this.comments = postEntity.getCommentsCnt();
        this.regDate = postEntity.getRegDate();
        this.isMyPost = authorUserId.equals(userId);
    }
}
