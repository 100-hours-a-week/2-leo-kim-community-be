package org.community.service.post;

import jakarta.servlet.http.HttpServletRequest;
import org.community.dto.request.post.PostCreateRequest;
import org.community.dto.request.post.PostUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface PostService {
    ResponseEntity<ApiResponse> getPosts(int page, int size);
    ResponseEntity<ApiResponse> createPosts(UserEntity user, PostCreateRequest postCreateRequest, MultipartFile postImage);
    ResponseEntity<ApiResponse> getPostDetail(UserEntity user, PostEntity post);
    ResponseEntity<ApiResponse> updatePost(Long postId, PostUpdateRequest postUpdateRequest, MultipartFile postImage);
    ResponseEntity<ApiResponse> deletePost(PostEntity post);
}
