package org.community.respository.like;

import org.community.entity.like.LikedUserEntity;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedUserRepository extends JpaRepository<LikedUserEntity, Long> {
    Optional<LikedUserEntity> findByUserAndPost(UserEntity user, PostEntity post);
}
