package org.community.entity.like;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts_liked_users")
public class LikedUserEntity {

    @EmbeddedId
    private LikedUserId likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Builder
    public LikedUserEntity(UserEntity user, PostEntity post){
        this.user = user;
        this.post = post;
        this.likeId = new LikedUserId(user.getUserId(), post.getPostId());
    }
}
