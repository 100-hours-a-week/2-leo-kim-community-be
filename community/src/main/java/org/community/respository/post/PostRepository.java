package org.community.respository.post;

import lombok.NonNull;
import org.community.entity.post.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @NonNull
    Page<PostEntity> findAll(@NonNull Pageable pageable);
}
