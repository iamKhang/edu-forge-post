package vn.iuh.ktpm.eduforgepost.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iuh.ktpm.eduforgepost.dto.ApiResponse;
import vn.iuh.ktpm.eduforgepost.dto.SeriesRequest;
import vn.iuh.ktpm.eduforgepost.dto.SeriesResponse;
import vn.iuh.ktpm.eduforgepost.service.SeriesService;

import java.util.UUID;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {
    
    private final SeriesService seriesService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<SeriesResponse>> createSeries(@Valid @RequestBody SeriesRequest seriesRequest) {
        SeriesResponse createdSeries = seriesService.createSeries(seriesRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Series created successfully", createdSeries));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeriesResponse>> getSeriesById(@PathVariable String id) {
        SeriesResponse series = seriesService.getSeriesById(id);
        return ResponseEntity.ok(ApiResponse.success(series));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SeriesResponse>>> getAllSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SeriesResponse> series = seriesService.getAllSeries(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(series));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<SeriesResponse>>> getSeriesByUserId(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SeriesResponse> series = seriesService.getSeriesByUserId(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(series));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SeriesResponse>>> searchSeries(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SeriesResponse> series = seriesService.searchSeries(keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(series));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeriesResponse>> updateSeries(
            @PathVariable String id,
            @Valid @RequestBody SeriesRequest seriesRequest) {
        
        SeriesResponse updatedSeries = seriesService.updateSeries(id, seriesRequest);
        return ResponseEntity.ok(ApiResponse.success("Series updated successfully", updatedSeries));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeries(
            @PathVariable String id,
            @RequestParam UUID userId) {
        
        seriesService.deleteSeries(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Series deleted successfully", null));
    }
    
    @PostMapping("/{seriesId}/posts/{postId}")
    public ResponseEntity<ApiResponse<SeriesResponse>> addPostToSeries(
            @PathVariable String seriesId,
            @PathVariable String postId,
            @RequestParam int order,
            @RequestParam UUID userId) {
        
        SeriesResponse updatedSeries = seriesService.addPostToSeries(seriesId, postId, order, userId);
        return ResponseEntity.ok(ApiResponse.success("Post added to series successfully", updatedSeries));
    }
    
    @DeleteMapping("/{seriesId}/posts/{postId}")
    public ResponseEntity<ApiResponse<SeriesResponse>> removePostFromSeries(
            @PathVariable String seriesId,
            @PathVariable String postId,
            @RequestParam UUID userId) {
        
        SeriesResponse updatedSeries = seriesService.removePostFromSeries(seriesId, postId, userId);
        return ResponseEntity.ok(ApiResponse.success("Post removed from series successfully", updatedSeries));
    }
    
    @PutMapping("/{seriesId}/posts/{postId}/order")
    public ResponseEntity<ApiResponse<SeriesResponse>> updatePostOrderInSeries(
            @PathVariable String seriesId,
            @PathVariable String postId,
            @RequestParam int newOrder,
            @RequestParam UUID userId) {
        
        SeriesResponse updatedSeries = seriesService.updatePostOrderInSeries(seriesId, postId, newOrder, userId);
        return ResponseEntity.ok(ApiResponse.success("Post order updated successfully", updatedSeries));
    }
}
