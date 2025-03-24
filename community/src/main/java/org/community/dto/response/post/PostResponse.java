package org.community.dto.response.post;

import lombok.Getter;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

import java.util.Date;

@Getter
public class PostResponse {
    private final Long postId;
    private final User user;
    private final String title;
    private final String contents;
    private final String postImagePath;
    private final Date regDate;
    private final Integer views;
    private final int likes; // 좋아요 개수 추가
    private final int comments;

    public PostResponse(PostEntity post){
        UserEntity user = post.getUser();
        this.postId = post.getPostId();
        this.user = User.builder()
                .nickname(user.getNickname())
                .profileImagePath(user.getProfileImagePath())
                .build();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.postImagePath = post.getPostImagePath();
        this.regDate = post.getRegDate();
        this.views = post.getViews();
        this.likes = post.getLikesCnt();
        this.comments = post.getCommentsCnt();
    }
}
