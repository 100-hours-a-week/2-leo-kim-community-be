package org.community.controller.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.community.dto.request.post.PostCreateRequest;
import org.community.dto.request.post.PostUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.service.file.FileUploadService;
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
    private final FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        return postService.getPosts(page,size);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createPosts(HttpServletRequest request
            ,@RequestPart("data") PostCreateRequest postCreateRequest
            ,@RequestPart(value = "postImage", required = false) MultipartFile postImage){
        String imagePath = null;
        if (postImage != null && !postImage.isEmpty()) {
            imagePath = fileUploadService.saveImage(postImage, false);
        }

        return postService.createPosts(request,postCreateRequest, imagePath);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostDetail(HttpServletRequest request, @PathVariable Long postId){
        return postService.getPostDetail(request,postId);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long postId
            ,@RequestPart("data") PostUpdateRequest postUpdateRequest
            ,@RequestPart(value = "postImage", required = false) MultipartFile postImage){
        String imagePath = null;
        if (postImage != null && !postImage.isEmpty()) {
            imagePath = fileUploadService.saveImage(postImage, false);
        }

        return postService.updatePost(postId,postUpdateRequest,imagePath);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId){
        return postService.deletePost(postId);
    }
}
