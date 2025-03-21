package org.community.entity.post;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.community.entity.comment.CommentEntity;
import org.community.entity.like.LikedUserEntity;
import org.community.entity.user.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posts")
@ToString
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", unique = true, nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private String image;

    @CreationTimestamp
    @Column(name = "reg_date", updatable = false)
    private Date regDate;

    @UpdateTimestamp
    @Column(name = "mod_date")
    private Date modDate;

    @Column(name = "views", nullable = false)
    private Integer views;

    @Column(name = "likes_cnt", nullable = false)
    private Integer likesCnt;

    @Column(name = "comments_cnt", nullable = false)
    private Integer commentsCnt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikedUserEntity> likedUsers = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    @Builder
    public PostEntity(UserEntity user, String title, String contents, String image) {
        this.user = user;
        this.title = title;
        this.contents = contents;
        this.image = image;
        this.views = 0;
        this.likesCnt = 0;
        this.commentsCnt = 0;
    }
}
