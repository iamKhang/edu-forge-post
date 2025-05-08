package vn.iuh.ktpm.eduforgepost.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iuh.ktpm.eduforgepost.dto.ApiResponse;
import vn.iuh.ktpm.eduforgepost.dto.PostLikeRequest;
import vn.iuh.ktpm.eduforgepost.model.PostLike;
import vn.iuh.ktpm.eduforgepost.service.PostLikeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-likes")
@RequiredArgsConstructor
public class PostLikeController {
    
    private final PostLikeService postLikeService;
    
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(@Valid @RequestBody PostLikeRequest request) {
        boolean isLiked = postLikeService.toggleLike(request);
        String message = isLiked ? "Post liked successfully" : "Post unliked successfully";
        
        return ResponseEntity.ok(ApiResponse.success(message, isLiked));
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<PostLike>>> getLikesByPostId(@PathVariable String postId) {
        List<PostLike> likes = postLikeService.getLikesByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(likes));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PostLike>>> getLikesByUserId(@PathVariable String userId) {
        List<PostLike> likes = postLikeService.getLikesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(likes));
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkIfUserLikedPost(
            @RequestParam String postId,
            @RequestParam String userId) {
        
        boolean isLiked = postLikeService.checkIfUserLikedPost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }
}
