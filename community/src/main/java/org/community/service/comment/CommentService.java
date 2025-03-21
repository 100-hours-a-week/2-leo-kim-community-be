package org.community.service.comment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.community.common.user.UserResponseMessage;
import org.community.dto.request.comment.CommentCreateRequest;
import org.community.dto.request.comment.CommentUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.dto.response.post.Comment;
import org.community.entity.comment.CommentEntity;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.global.CustomException;
import org.community.respository.comment.CommentRepository;
import org.community.respository.post.PostRepository;
import org.community.respository.user.UserRepository;
import org.community.util.jwtutil.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> createComment(HttpServletRequest request, Long postId, CommentCreateRequest commentCreateRequest) {
        Long userId = jwtUtil.getUserIdFromJwt(request.getHeader("Authorization"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(UserResponseMessage.USER_NOT_FOUND));
//        user.getLikedPosts().size(); -> 이와 같이 종료된 Persistence Context내의 프록시 객체를 조회하려고 하면 LazyInitializationException !!!!
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));
        CommentEntity newComment = commentCreateRequest.toEntity(user, post);
        commentRepository.save(newComment);
        post.setCommentsCnt(post.getCommentsCnt()+1);
        return ApiResponse.response(UserResponseMessage.COMMENT_CREATED);
    }

    // PostService에서 호출
    public List<Comment> getCommentsByPost(Long userId, PostEntity post) {
        List<CommentEntity> commentEntityList = commentRepository.findAllByPost(post);
        return commentEntityList.stream().map(commentEntity -> new Comment(userId,commentEntity)).toList();
    }

    public ResponseEntity<ApiResponse> updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest) {
        CommentEntity targetComment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(UserResponseMessage.COMMENT_NOT_FOUND));
        targetComment.setContents(commentUpdateRequest.getContents());
        return ApiResponse.response(UserResponseMessage.COMMENT_UPDATED);
    }

    public ResponseEntity<ApiResponse> deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(UserResponseMessage.COMMENT_NOT_FOUND));
        Long postId = comment.getPost().getPostId();
        commentRepository.deleteById(commentId);
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));

        return ApiResponse.response(UserResponseMessage.COMMENT_DELETED);
    }
}
