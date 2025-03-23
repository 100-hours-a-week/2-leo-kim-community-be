package org.community.service.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.community.common.user.UserResponseMessage;
import org.community.dto.request.post.PostCreateRequest;
import org.community.dto.request.post.PostUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.dto.response.post.Comment;
import org.community.dto.response.post.PostDetailResponse;
import org.community.dto.response.post.PostResponse;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.post.PostRepository;
import org.community.respository.user.UserRepository;
import org.community.service.comment.CommentService;
import org.community.service.like.LikeService;
import org.community.util.jwtutil.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<PostEntity> pages = postRepository.findAll(pageable);

        List<PostResponse> postList = pages.getContent().stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        return ApiResponse.response(UserResponseMessage.POST_FETCH_SUCCESS, postList);
    }


    public ResponseEntity<ApiResponse> createPosts(HttpServletRequest request, PostCreateRequest postCreateRequest, String imagePath) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(UserResponseMessage.USER_NOT_FOUND)
        );
        PostEntity newPost = postCreateRequest.toEntity(user);
        newPost.setImage(imagePath);
        postRepository.save(newPost);
        return ApiResponse.response(UserResponseMessage.POST_CREATED);
    }

    public ResponseEntity<ApiResponse> getPostDetail(HttpServletRequest request, Long postId) {
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        PostDetailResponse responseBody = new PostDetailResponse(post, userId);

        List<Comment> commentList = commentService.getCommentsByPost(userId, post);
        responseBody.setCommentList(commentList);

        Boolean isLiked = likeService.getIsLiked(request, post);
        responseBody.setIsLiked(isLiked);
        post.setViews(post.getViews() + 1);
        return ApiResponse.response(UserResponseMessage.POST_FETCH_SUCCESS, responseBody);
    }

    public ResponseEntity<ApiResponse> updatePost(Long postId, PostUpdateRequest postUpdateRequest, String imagePath) {

        PostEntity post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException(UserResponseMessage.POST_NOT_FOUND)
        );

        // 기존 이미지 파일 삭제
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + post.getImage();
        File oldFile = new File(fullOldImagePath);

        if (oldFile.exists()) {
            boolean deleted = oldFile.delete();
            if (!deleted) {
                log.warn("기존 이미지 파일 삭제 실패: {}", fullOldImagePath);
            } else {
                log.info("기존 이미지 파일 삭제 성공: {}", fullOldImagePath);
            }
        }

        post.setTitle(postUpdateRequest.getTitle());
        post.setContents(postUpdateRequest.getContents());
        post.setImage(imagePath);

        return ApiResponse.response(UserResponseMessage.POST_UPDATED);
    }

    /// ////////

    public ResponseEntity<ApiResponse> deletePost(Long postId) {
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));

        // 기존 이미지 파일 삭제
        String fullOldImagePath = System.getProperty("user.dir") + "/community" + post.getImage();
        File oldFile = new File(fullOldImagePath);

        if (oldFile.exists()) {
            boolean deleted = oldFile.delete();
            if (!deleted) {
                log.warn("기존 이미지 파일 삭제 실패: {}", fullOldImagePath);
            } else {
                log.info("기존 이미지 파일 삭제 성공: {}", fullOldImagePath);
            }
        }

        postRepository.delete(post);
        return ApiResponse.response(UserResponseMessage.POST_DELETED);
    }
}
