package org.community.service.post;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.community.common.function.CommonFunctions;
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
import org.community.service.file.FileUploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final FileUploadService fileUploadService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final CommonFunctions commonFunctions;

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getPosts(int page, int size) {
        // pageable 조건에 맞는 page를 반환
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<PostEntity> pages = postRepository.findAll(pageable);

        // postList로 만들어 responseBody로 생성
        List<PostResponse> postList = pages.getContent().stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());

        return ApiResponse.response(UserResponseMessage.POST_FETCH_SUCCESS, postList);
    }

    @Transactional
    public ResponseEntity<ApiResponse> createPosts(UserEntity user, PostCreateRequest postCreateRequest, MultipartFile postImage) {
        // requestDto로 entity 뼈대 생성
        PostEntity newPost = postCreateRequest.toEntity(user);

        // 새 경로 저장
        String imagePath = commonFunctions.getImagePath(postImage, false);
        newPost.setPostImagePath(imagePath);
        postRepository.save(newPost);

        return ApiResponse.response(UserResponseMessage.POST_CREATED);
    }

    // 조회수 증가 로직때문에 transactional 필요
    @Transactional
    public ResponseEntity<ApiResponse> getPostDetail(UserEntity user, PostEntity post) {
        // postEntity와 (내 게시물인지확인용)userEntity로 responseDto 뼈대 생성
        PostDetailResponse responseBody = PostDetailResponse.builder()
                .postEntity(post)
                .myUserId(user.getUserId())
                .build();

        // 댓글목록 조회
        List<Comment> commentList = commentService.getCommentsByPost(user.getUserId(), post);
        responseBody.setCommentList(commentList);

        // 좋아요 여부
        Boolean isLiked = likeService.getIsLiked(user, post);
        responseBody.setIsLiked(isLiked);

        // 조회수 증가 처리
        post.setViews(post.getViews() + 1);

        return ApiResponse.response(UserResponseMessage.POST_FETCH_SUCCESS, responseBody);
    }

    @Transactional
    public ResponseEntity<ApiResponse> updatePost(Long postId, PostUpdateRequest postUpdateRequest, MultipartFile postImage) {
        // 기존 이미지 파일 삭제
        // 영속성 컨텍스트 이슈 때문에 update는 커스텀 어노테이션을 적용할 수 없다.
        // -> 트랜잭션 범위를 벗어난 detached postEntity이기 때문에
        // JPA의 dirty checking으로 update할 수 없음
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(UserResponseMessage.POST_NOT_FOUND));
        File oldFile = commonFunctions.getPostImage(post);
        fileUploadService.deleteImage(oldFile);

        // 새로운 데이터 덮어씌우기
        String imagePath = commonFunctions.getImagePath(postImage,false);
        post.setTitle(postUpdateRequest.getTitle());
        post.setContents(postUpdateRequest.getContents());
        post.setPostImagePath(imagePath);

        return ApiResponse.response(UserResponseMessage.POST_UPDATED);
    }

    @Transactional
    public ResponseEntity<ApiResponse> deletePost(PostEntity post) {
        // 기존 이미지 파일 삭제
        File oldFile = commonFunctions.getPostImage(post);
        fileUploadService.deleteImage(oldFile);

        // db에 적용
        postRepository.delete(post);
        return ApiResponse.response(UserResponseMessage.POST_DELETED);
    }
}
