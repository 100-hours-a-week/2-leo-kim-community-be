package org.community.controller.comment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.community.dto.request.comment.CommentCreateRequest;
import org.community.dto.request.comment.CommentUpdateRequest;
import org.community.dto.response.ApiResponse;
import org.community.service.comment.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse> createComment(HttpServletRequest request, @PathVariable Long postId, @RequestBody CommentCreateRequest commentCreateRequest){
        return commentService.createComment(request,postId,commentCreateRequest);
    }

    // 이 친구를 단일로 FE에서 부를 이유가 없음.
//    @GetMapping("/posts/{postId}/comments")
//    public ResponseEntity<ApiResponse> getCommentsByPostId(HttpServletRequest request, @PathVariable Long postId){
//        return commentService.getCommentsByPostId(request,postId);
//    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest commentUpdateRequest){
        return commentService.updateComment(commentId,commentUpdateRequest);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId){
        return commentService.deleteComment(commentId);
    }


}
