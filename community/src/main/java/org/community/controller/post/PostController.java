package org.community.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.community.annotation.CurrentPost;
import org.community.annotation.CurrentPostWithUser;
import org.community.annotation.CurrentUser;
import org.community.dto.request.post.PostCreateRequest;
import org.community.dto.request.post.PostUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.entity.post.PostEntity;
import org.community.entity.user.UserEntity;
import org.community.service.post.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        return postService.getPosts(page,size);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createPosts(@CurrentUser UserEntity user
            ,@RequestPart("data") PostCreateRequest postCreateRequest
            ,@RequestPart(value = "postImage", required = false) MultipartFile postImage){


        return postService.createPosts(user,postCreateRequest, postImage);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostDetail(@CurrentUser UserEntity user, @CurrentPostWithUser PostEntity post){
        return postService.getPostDetail(user,post);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long postId
            ,@RequestPart("data") PostUpdateRequest postUpdateRequest
            ,@RequestPart(value = "postImage", required = false) MultipartFile postImage){


        return postService.updatePost(postId,postUpdateRequest,postImage);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@CurrentPost PostEntity post){
        return postService.deletePost(post);
    }
}
