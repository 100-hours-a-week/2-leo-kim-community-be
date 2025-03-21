package org.community.service.like;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.dto.response.ApiResponse;
import org.community.entity.like.LikedUserEntity;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.like.LikedUserRepository;
import org.community.respository.post.PostRepository;
import org.community.respository.user.UserRepository;
import org.community.util.jwtutil.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikedUserRepository likedUserRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    public Boolean getIsLiked(HttpServletRequest request, PostEntity post) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user =userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));

        return likedUserRepository.findByUserAndPost(user,post).isPresent();
    }


    public ResponseEntity<ApiResponse> toggleLike(HttpServletRequest request, Long postId) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));
        Optional<LikedUserEntity> likedUserEntity = likedUserRepository.findByUserAndPost(user,post);

        if(likedUserEntity.isEmpty()) {
            LikedUserEntity newLikedUserEntity = LikedUserEntity.builder()
                    .user(user)
                    .post(post)
                    .build();

            likedUserRepository.save(newLikedUserEntity);
            post.setLikesCnt(post.getLikesCnt()+1);
            return ApiResponse.response(UserResponseMessage.LIKE_ADDED);
        }
        else{
            likedUserRepository.delete(likedUserEntity.get());
            post.setLikesCnt(post.getLikesCnt()-1);
            return ApiResponse.response(UserResponseMessage.LIKE_DELETED);
        }
    }
}
