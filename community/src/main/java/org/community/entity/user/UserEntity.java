package org.community.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.community.entity.like.LikedUserEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_pic")
    private String profilePic;

    // Builder 어노테이션 방식
    @Builder
    public UserEntity(String email, String nickname, String password, String profilePic) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profilePic = profilePic;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikedUserEntity> likedPosts = new ArrayList<>();
}
