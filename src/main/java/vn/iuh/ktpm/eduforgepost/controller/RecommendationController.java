package vn.iuh.ktpm.eduforgepost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iuh.ktpm.eduforgepost.dto.RecommendedPostDto;
import vn.iuh.ktpm.eduforgepost.service.RecommendationImportService;
import vn.iuh.ktpm.eduforgepost.service.RecommendationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private RecommendationImportService recommendationImportService;

    /**
     * Get paginated list of recommended posts for a user
     * 
     * @param userId the user ID
     * @param page page number (0-based)
     * @param size page size
     * @return page of recommended posts
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<RecommendedPostDto>> getRecommendedPosts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<RecommendedPostDto> recommendedPosts = recommendationService.getRecommendedPostsForUser(userId, pageRequest);
        
        return ResponseEntity.ok(recommendedPosts);
    }
    
    /**
     * Manually trigger the import of recommendations from the external API
     * 
     * @return result of the import process
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importRecommendations() {
        boolean success = recommendationImportService.importRecommendations();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? 
                "Recommendations imported successfully" : 
                "Failed to import recommendations");
        
        return ResponseEntity.ok(response);
    }
} 