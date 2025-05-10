package vn.iuh.ktpm.eduforgepost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iuh.ktpm.eduforgepost.dto.RecommendedPostDto;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.Recommendation;
import vn.iuh.ktpm.eduforgepost.repository.PostRepository;
import vn.iuh.ktpm.eduforgepost.repository.RecommendationRepository;

import java.util.ArrayList;
import java.util.Comparator;
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
     * @return page of recommended post DTOs
     */
    public Page<RecommendedPostDto> getRecommendedPostsForUser(String userId, Pageable pageable) {
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
        
        // Create DTOs
        List<RecommendedPostDto> recommendedPostDtos = new ArrayList<>();
        for (Recommendation.RecommendedPost rec : pageRecommendations) {
            Post post = postMap.get(rec.getPostId());
            if (post != null) {
                RecommendedPostDto dto = RecommendedPostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .coverImage(post.getCoverImage())
                        .content(post.getContent())
                        .score(rec.getScore())
                        .build();
                recommendedPostDtos.add(dto);
            }
        }
        
        return new PageImpl<>(recommendedPostDtos, pageable, allRecommendations.size());
    }
} 