package org.community.dto.response.post;

import lombok.Getter;
import org.community.entity.post.PostEntity;

import java.util.Date;

@Getter
public class PostResponse {
    private final Long postId;
    private final String title;
    private final String contents;
    private final String image;
    private final Date regDate;
    private final Integer views;
    private final int likes; // 좋아요 개수 추가
    private final int comments;

    public PostResponse(PostEntity post){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.image = post.getImage();
        this.regDate = post.getRegDate();
        this.views = post.getViews();
        this.likes = post.getLikesCount();
        this.comments = post.getCommentsCount();
    }
}
