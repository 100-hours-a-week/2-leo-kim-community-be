package org.community.respository.post;

import lombok.NonNull;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @NonNull
    Page<PostEntity> findAll(@NonNull Pageable pageable);
    List<PostEntity> findAllByUser(UserEntity user);
}
