package org.community.dto.response.post;

import lombok.Builder;

import java.util.Date;

@Builder
public class Post {
    private Long postId;
    private Long authorUserId;
    private String title;
    private String contents;
    private String image;
    private Integer views;
    private Date regDate;
}
