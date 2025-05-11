package vn.iuh.ktpm.eduforgepost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iuh.ktpm.eduforgepost.dto.PostResponse;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.Recommendation;
import vn.iuh.ktpm.eduforgepost.model.Series;
import vn.iuh.ktpm.eduforgepost.repository.PostRepository;
import vn.iuh.ktpm.eduforgepost.repository.RecommendationRepository;
import vn.iuh.ktpm.eduforgepost.repository.SeriesRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private SeriesRepository seriesRepository;

    /**
     * Save recommendations from recommendation API response
     * 
     * @param userId the user ID
     * @param recommendedPosts list of recommended posts with scores
     * @return the saved recommendation
     */
    public Recommendation saveRecommendations(String userId, List<Recommendation.RecommendedPost> recommendedPosts) {
        Optional<Recommendation> existingRecommendation = recommendationRepository.findByUserId(userId);
        
        Recommendation recommendation;
        if (existingRecommendation.isPresent()) {
            recommendation = existingRecommendation.get();
            recommendation.setHybridRecommendations(recommendedPosts);
        } else {
            recommendation = Recommendation.builder()
                    .userId(userId)
                    .hybridRecommendations(recommendedPosts)
                    .build();
        }
        
        return recommendationRepository.save(recommendation);
    }

    /**
     * Get recommended posts for a user with pagination
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of recommended posts in standard PostResponse format
     */
    public Page<PostResponse> getRecommendedPostsForUser(String userId, Pageable pageable, String currentUserId) {
        // Get recommendation for the user
        Optional<Recommendation> recommendationOpt = recommendationRepository.findByUserId(userId);
        
        if (recommendationOpt.isEmpty()) {
            return Page.empty(pageable);
        }
        
        Recommendation recommendation = recommendationOpt.get();
        List<Recommendation.RecommendedPost> allRecommendations = recommendation.getHybridRecommendations();
        
        // Sort recommendations by score (highest first)
        allRecommendations.sort(Comparator.comparing(Recommendation.RecommendedPost::getScore).reversed());
        
        // Get post IDs for the current page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRecommendations.size());
        
        if (start >= allRecommendations.size()) {
            return Page.empty(pageable);
        }
        
        List<Recommendation.RecommendedPost> pageRecommendations = allRecommendations.subList(start, end);
        
        // Get all post IDs
        List<String> postIds = pageRecommendations.stream()
                .map(Recommendation.RecommendedPost::getPostId)
                .collect(Collectors.toList());
        
        // Get all posts
        List<Post> posts = postRepository.findAllByIdIn(postIds);
        
        // Create a map of post ID to post
        Map<String, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));
        
        // Get all series IDs
        List<String> seriesIds = posts.stream()
                .map(Post::getSeriesId)
                .filter(seriesId -> seriesId != null && !seriesId.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        
        // Get all series
        Map<String, Series> seriesMap = new HashMap<>();
        if (!seriesIds.isEmpty()) {
            List<Series> seriesList = seriesRepository.findAllById(seriesIds);
            seriesMap = seriesList.stream()
                    .collect(Collectors.toMap(Series::getId, series -> series));
        }
        
        // Create PostResponse objects in the same order as the recommendations
        List<PostResponse> postResponses = new ArrayList<>();
        for (Recommendation.RecommendedPost rec : pageRecommendations) {
            Post post = postMap.get(rec.getPostId());
            if (post != null) {
                PostResponse response = mapPostToPostResponse(post, seriesMap, currentUserId);
                postResponses.add(response);
            }
        }
        
        return new PageImpl<>(postResponses, pageable, allRecommendations.size());
    }
    
    /**
     * Maps a Post entity to a PostResponse DTO
     */
    private PostResponse mapPostToPostResponse(Post post, Map<String, Series> seriesMap, String currentUserId) {
        boolean likedByCurrentUser = currentUserId != null && post.getLikes() != null && 
                post.getLikes().stream().anyMatch(like -> like.getUserId().equals(currentUserId));
        
        boolean viewedByCurrentUser = currentUserId != null && post.getViews() != null && 
                post.getViews().stream().anyMatch(view -> view.getUserId().equals(currentUserId));

        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .coverImage(post.getCoverImage())
                .tags(post.getTags())
                .totalLikes(post.getLikes() != null ? post.getLikes().size() : 0)
                .totalViews(post.getViews() != null ? post.getViews().size() : 0)
                .likedByCurrentUser(likedByCurrentUser)
                .viewedByCurrentUser(viewedByCurrentUser)
                .isPublished(post.isPublished())
                .seriesId(post.getSeriesId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
        
        // Add series information if post is part of a series
        if (post.getSeriesId() != null && !post.getSeriesId().isEmpty() && seriesMap.containsKey(post.getSeriesId())) {
            Series series = seriesMap.get(post.getSeriesId());
            response.setSeriesTitle(series.getTitle());
            
            // Find the order in series
            for (Series.SeriesItem item : series.getPosts()) {
                if (item.getPostId().equals(post.getId())) {
                    response.setOrderInSeries(item.getOrder());
                    break;
                }
            }
        }
        
        return response;
    }
} 