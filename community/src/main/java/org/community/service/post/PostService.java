package org.community.service.post;

import jakarta.annotation.Nullable;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public interface PostService {
    ResponseEntity<ApiResponse> getPosts(int page, int size);
    ResponseEntity<ApiResponse> createPosts(UserEntity user, PostCreateRequest postCreateRequest, @Nullable MultipartFile postImage);
    ResponseEntity<ApiResponse> getPostDetail(HttpServletRequest request, Long postId);
    ResponseEntity<ApiResponse> updatePost(Long postId, PostUpdateRequest postUpdateRequest, String imagePath);
    ResponseEntity<ApiResponse> deletePost(Long postId);
}
