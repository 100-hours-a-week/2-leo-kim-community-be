package org.community.dto.response.post;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PostDetailResponse {
    private final User author;
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

    @Builder
    public PostDetailResponse(PostEntity postEntity, Long myUserId){
        UserEntity user = postEntity.getUser();
        this.author = User.builder()
                .nickname(user.getNickname())
                .profileImagePath(user.getProfileImagePath())
                .build();
        this.title = postEntity.getTitle();
        this.contents = postEntity.getContents();
        this.image = postEntity.getPostImagePath();
        this.views = postEntity.getViews()+1;
        this.likes = postEntity.getLikesCnt();
        this.comments = postEntity.getCommentsCnt();
        this.regDate = postEntity.getRegDate();
        this.isMyPost = user.getUserId().equals(myUserId);
    }
}
