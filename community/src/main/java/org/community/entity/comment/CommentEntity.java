package org.community.entity.comment;

import jakarta.persistence.*;
import lombok.*;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "post_comments")
@ToString
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false, unique = true)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(name = "comment_contents", nullable = false)
    private String contents;

    @CreationTimestamp
    @Column(name = "comment_reg_date")
    private Date commentRegDate;

    @UpdateTimestamp
    @Column(name = "comment_mod_date")
    private Date commentModDate;

    @Builder
    public CommentEntity(UserEntity user, PostEntity post, String contents){
        this.user = user;
        this.post = post;
        this.contents = contents;
    }
}
