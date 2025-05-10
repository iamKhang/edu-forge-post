package vn.iuh.ktpm.eduforgepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iuh.ktpm.eduforgepost.dto.SeriesRequest;
import vn.iuh.ktpm.eduforgepost.dto.SeriesResponse;
import vn.iuh.ktpm.eduforgepost.exception.ResourceNotFoundException;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.Series;
import vn.iuh.ktpm.eduforgepost.repository.PostRepository;
import vn.iuh.ktpm.eduforgepost.repository.SeriesRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {
    
    private final SeriesRepository seriesRepository;
    private final PostRepository postRepository;
    
    @Transactional
    public SeriesResponse createSeries(SeriesRequest seriesRequest) {
        // Validate that all posts exist and belong to the user
        validatePostsExistAndBelongToUser(seriesRequest.getPosts().stream()
                .map(SeriesRequest.SeriesItemDto::getPostId)
                .collect(Collectors.toList()), seriesRequest.getUserId());
        
        // Create series
        Series series = Series.builder()
                .userId(seriesRequest.getUserId())
                .title(seriesRequest.getTitle())
                .description(seriesRequest.getDescription())
                .coverImage(seriesRequest.getCoverImage())
                .isPublished(seriesRequest.isPublished())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Add posts to series
        List<Series.SeriesItem> seriesItems = seriesRequest.getPosts().stream()
                .map(item -> Series.SeriesItem.builder()
                        .postId(item.getPostId())
                        .order(item.getOrder())
                        .build())
                .collect(Collectors.toList());
        
        series.setPosts(seriesItems);
        
        // Save series
        Series savedSeries = seriesRepository.save(series);
        
        // Update posts with series ID
        updatePostsWithSeriesId(savedSeries.getId(), seriesRequest.getPosts().stream()
                .map(SeriesRequest.SeriesItemDto::getPostId)
                .collect(Collectors.toList()));
        
        return mapToSeriesResponse(savedSeries);
    }
    
    public SeriesResponse getSeriesById(String id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", id));
        
        return mapToSeriesResponse(series);
    }
    
    public Page<SeriesResponse> getAllSeries(Pageable pageable) {
        Page<Series> series = seriesRepository.findByIsPublishedTrue(pageable);
        return series.map(this::mapToSeriesResponse);
    }
    
    public Page<SeriesResponse> getSeriesByUserId(String userId, Pageable pageable) {
        Page<Series> series = seriesRepository.findByUserId(userId, pageable);
        return series.map(this::mapToSeriesResponse);
    }
    
    public Page<SeriesResponse> searchSeries(String keyword, Pageable pageable) {
        Page<Series> series = seriesRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
        return series.map(this::mapToSeriesResponse);
    }
    
    @Transactional
    public SeriesResponse updateSeries(String id, SeriesRequest seriesRequest) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", id));
        
        // Verify that the user is the owner of the series
        if (!series.getUserId().equals(seriesRequest.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to update this series");
        }
        
        // Validate that all posts exist and belong to the user
        validatePostsExistAndBelongToUser(seriesRequest.getPosts().stream()
                .map(SeriesRequest.SeriesItemDto::getPostId)
                .collect(Collectors.toList()), seriesRequest.getUserId());
        
        // Get current posts in series
        List<String> currentPostIds = series.getPosts().stream()
                .map(Series.SeriesItem::getPostId)
                .collect(Collectors.toList());
        
        // Get new posts in series
        List<String> newPostIds = seriesRequest.getPosts().stream()
                .map(SeriesRequest.SeriesItemDto::getPostId)
                .collect(Collectors.toList());
        
        // Remove series ID from posts that are no longer in the series
        List<String> postsToRemove = currentPostIds.stream()
                .filter(postId -> !newPostIds.contains(postId))
                .collect(Collectors.toList());
        
        if (!postsToRemove.isEmpty()) {
            removeSeriesIdFromPosts(postsToRemove);
        }
        
        // Add series ID to new posts
        List<String> postsToAdd = newPostIds.stream()
                .filter(postId -> !currentPostIds.contains(postId))
                .collect(Collectors.toList());
        
        if (!postsToAdd.isEmpty()) {
            updatePostsWithSeriesId(id, postsToAdd);
        }
        
        // Update series
        series.setTitle(seriesRequest.getTitle());
        series.setDescription(seriesRequest.getDescription());
        series.setCoverImage(seriesRequest.getCoverImage());
        series.setPublished(seriesRequest.isPublished());
        series.setUpdatedAt(LocalDateTime.now());
        
        // Update posts in series
        List<Series.SeriesItem> seriesItems = seriesRequest.getPosts().stream()
                .map(item -> Series.SeriesItem.builder()
                        .postId(item.getPostId())
                        .order(item.getOrder())
                        .build())
                .collect(Collectors.toList());
        
        series.setPosts(seriesItems);
        
        // Save series
        Series updatedSeries = seriesRepository.save(series);
        
        return mapToSeriesResponse(updatedSeries);
    }
    
    @Transactional
    public void deleteSeries(String id, String userId) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", id));
        
        // Verify that the user is the owner of the series
        if (!series.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this series");
        }
        
        // Remove series ID from all posts in the series
        List<String> postIds = series.getPosts().stream()
                .map(Series.SeriesItem::getPostId)
                .collect(Collectors.toList());
        
        if (!postIds.isEmpty()) {
            removeSeriesIdFromPosts(postIds);
        }
        
        // Delete series
        seriesRepository.delete(series);
    }
    
    @Transactional
    public SeriesResponse addPostToSeries(String seriesId, String postId, int order, String userId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", seriesId));
        
        // Verify that the user is the owner of the series
        if (!series.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this series");
        }
        
        // Verify that the post exists and belongs to the user
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to add this post to the series");
        }
        
        // Check if post is already in the series
        boolean postAlreadyInSeries = series.getPosts().stream()
                .anyMatch(item -> item.getPostId().equals(postId));
        
        if (postAlreadyInSeries) {
            // Update order if post is already in series
            series.getPosts().forEach(item -> {
                if (item.getPostId().equals(postId)) {
                    item.setOrder(order);
                }
            });
        } else {
            // Add post to series
            series.getPosts().add(Series.SeriesItem.builder()
                    .postId(postId)
                    .order(order)
                    .build());
            
            // Update post with series ID
            post.setSeriesId(seriesId);
            postRepository.save(post);
        }
        
        // Save series
        Series updatedSeries = seriesRepository.save(series);
        
        return mapToSeriesResponse(updatedSeries);
    }
    
    @Transactional
    public SeriesResponse removePostFromSeries(String seriesId, String postId, String userId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", seriesId));
        
        // Verify that the user is the owner of the series
        if (!series.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this series");
        }
        
        // Verify that the post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        // Remove post from series
        series.setPosts(series.getPosts().stream()
                .filter(item -> !item.getPostId().equals(postId))
                .collect(Collectors.toList()));
        
        // Remove series ID from post
        post.setSeriesId(null);
        postRepository.save(post);
        
        // Save series
        Series updatedSeries = seriesRepository.save(series);
        
        return mapToSeriesResponse(updatedSeries);
    }
    
    @Transactional
    public SeriesResponse updatePostOrderInSeries(String seriesId, String postId, int newOrder, String userId) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "id", seriesId));
        
        // Verify that the user is the owner of the series
        if (!series.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this series");
        }
        
        // Verify that the post exists and is in the series
        boolean postInSeries = series.getPosts().stream()
                .anyMatch(item -> item.getPostId().equals(postId));
        
        if (!postInSeries) {
            throw new ResourceNotFoundException("Post", "id", postId + " in series " + seriesId);
        }
        
        // Update post order
        series.getPosts().forEach(item -> {
            if (item.getPostId().equals(postId)) {
                item.setOrder(newOrder);
            }
        });
        
        // Save series
        Series updatedSeries = seriesRepository.save(series);
        
        return mapToSeriesResponse(updatedSeries);
    }
    
    private void validatePostsExistAndBelongToUser(List<String> postIds, String userId) {
        if (postIds.isEmpty()) {
            return;
        }
        
        List<Post> posts = postRepository.findAllById(postIds);
        
        // Check if all posts exist
        if (posts.size() != postIds.size()) {
            throw new ResourceNotFoundException("Some posts do not exist");
        }
        
        // Check if all posts belong to the user
        boolean allPostsBelongToUser = posts.stream()
                .allMatch(post -> post.getUserId().equals(userId));
        
        if (!allPostsBelongToUser) {
            throw new IllegalArgumentException("You are not authorized to add some of these posts to the series");
        }
    }
    
    private void updatePostsWithSeriesId(String seriesId, List<String> postIds) {
        if (postIds.isEmpty()) {
            return;
        }
        
        List<Post> posts = postRepository.findAllById(postIds);
        posts.forEach(post -> post.setSeriesId(seriesId));
        postRepository.saveAll(posts);
    }
    
    private void removeSeriesIdFromPosts(List<String> postIds) {
        if (postIds.isEmpty()) {
            return;
        }
        
        List<Post> posts = postRepository.findAllById(postIds);
        posts.forEach(post -> post.setSeriesId(null));
        postRepository.saveAll(posts);
    }
    
    private SeriesResponse mapToSeriesResponse(Series series) {
        // Get all posts in the series
        List<String> postIds = series.getPosts().stream()
                .map(Series.SeriesItem::getPostId)
                .collect(Collectors.toList());
        
        Map<String, Post> postsMap = postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));
        
        // Map series items to DTOs
        List<SeriesResponse.SeriesItemDto> seriesItems = new ArrayList<>();
        
        for (Series.SeriesItem item : series.getPosts()) {
            Post post = postsMap.get(item.getPostId());
            if (post != null) {
                seriesItems.add(SeriesResponse.SeriesItemDto.builder()
                        .postId(item.getPostId())
                        .postTitle(post.getTitle())
                        .postCoverImage(post.getCoverImage())
                        .order(item.getOrder())
                        .build());
            }
        }
        
        // Sort series items by order
        seriesItems.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        
        return SeriesResponse.builder()
                .id(series.getId())
                .userId(series.getUserId())
                .title(series.getTitle())
                .description(series.getDescription())
                .coverImage(series.getCoverImage())
                .posts(seriesItems)
                .isPublished(series.isPublished())
                .createdAt(series.getCreatedAt())
                .updatedAt(series.getUpdatedAt())
                .build();
    }

    public Page<Series> getRawSeriesForTraining(Pageable pageable) {
        return seriesRepository.findAll(pageable);
    }

    public List<Series> getAllRawSeriesForTraining() {
        return seriesRepository.findAll();
    }

    public Map<String, Object> getTrainingDataStatistics() {
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalSeries", seriesRepository.count());
        statistics.put("totalPublishedSeries", seriesRepository.countByIsPublishedTrue());
        statistics.put("totalUnpublishedSeries", seriesRepository.countByIsPublishedFalse());
        statistics.put("averagePostsPerSeries", calculateAveragePostsPerSeries());
        return statistics;
    }

    private double calculateAveragePostsPerSeries() {
        List<Series> allSeries = seriesRepository.findAll();
        if (allSeries.isEmpty()) {
            return 0.0;
        }
        double totalPosts = allSeries.stream()
                .mapToInt(series -> series.getPosts().size())
                .sum();
        return totalPosts / allSeries.size();
    }
}
