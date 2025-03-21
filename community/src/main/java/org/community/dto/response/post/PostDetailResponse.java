package org.community.dto.response.post;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    public PostDetailResponse(PostEntity postEntity, Long userId){
        UserEntity user = postEntity.getUser();
        this.author = User.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfilePic())
                .build();
        this.title = postEntity.getTitle();
        this.contents = postEntity.getContents();
        this.image = postEntity.getImage();
        this.views = postEntity.getViews();
        this.likes = postEntity.getLikesCnt();
        this.comments = postEntity.getCommentsCnt();
        this.regDate = postEntity.getRegDate();
        this.isMyPost = user.getUserId().equals(userId);
    }
}
