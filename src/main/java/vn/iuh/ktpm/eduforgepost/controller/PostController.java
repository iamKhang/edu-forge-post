package vn.iuh.ktpm.eduforgepost.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.iuh.ktpm.eduforgepost.dto.ApiResponse;
import vn.iuh.ktpm.eduforgepost.dto.PostRequest;
import vn.iuh.ktpm.eduforgepost.dto.PostResponse;
import vn.iuh.ktpm.eduforgepost.service.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/series/{seriesId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsBySeriesId(
            @PathVariable String seriesId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getPostsBySeriesId(seriesId, pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/no-series")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsWithoutSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getPostsWithoutSeries(pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/user/{userId}/no-series")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getUserPostsWithoutSeries(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getUserPostsWithoutSeries(userId, pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest postRequest) {
        PostResponse createdPost = postService.createPost(postRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", createdPost));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(
            @PathVariable String id,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        PostResponse post = postService.getPostById(id, userIdToUse);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getAllPosts(pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PostResponse> posts = postService.getPostsByUserId(userId, pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.searchPosts(keyword, pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String currentUserId) {

        // If currentUserId is not provided, use a default UUID to avoid null issues
        String userIdToUse = (currentUserId != null) ? currentUserId : "00000000-0000-0000-0000-000000000000";

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.getPostsByTag(tag, pageable, userIdToUse);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostRequest postRequest) {

        PostResponse updatedPost = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable String id,
            @RequestParam String userId) {

        postService.deletePost(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<PostResponse>> addView(
            @PathVariable String id,
            @RequestParam String userId) {
        PostResponse post = postService.addView(id, userId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<PostResponse>> toggleLike(
            @PathVariable String id,
            @RequestParam String userId) {
        PostResponse post = postService.toggleLike(id, userId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }
}
