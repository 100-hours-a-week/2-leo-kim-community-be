package org.community.controller.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.community.dto.request.post.PostCreateRequest;
import org.community.dto.request.post.PostUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.service.post.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        log.info("{} {}", page, size);
        return postService.getPosts(page,size);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPosts(HttpServletRequest request, @RequestBody PostCreateRequest postCreateRequest){
        return postService.createPosts(request,postCreateRequest);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse> viewPost(@PathVariable Long postId){
        return postService.viewPost(postId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostDetail(HttpServletRequest request, @PathVariable Long postId){
        return postService.getPostDetail(request,postId);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest postUpdateRequest){
        return postService.updatePost(postId,postUpdateRequest);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId){
        return postService.deletePost(postId);
    }
}
