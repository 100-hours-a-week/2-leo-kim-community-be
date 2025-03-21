package org.community.controller.like;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.community.dto.response.ApiResponse;
import org.community.service.like.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;

    // PostService가 호출할거임 필요 x
//    @GetMapping("/{postId}/like")
//    public ResponseEntity<ApiResponse> getIsLiked(HttpServletRequest request, @PathVariable Long postId){
//        return likeService.getIsLiked(request,postId);
//    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> toggleLike(HttpServletRequest request, @PathVariable Long postId){
        return likeService.toggleLike(request, postId);
    }
}
