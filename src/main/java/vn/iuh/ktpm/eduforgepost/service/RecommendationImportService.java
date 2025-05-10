package vn.iuh.ktpm.eduforgepost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.iuh.ktpm.eduforgepost.model.Recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationImportService {

    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Fetch recommendations from external API and save to database
     * 
     * @return true if successful, false otherwise
     */
    public boolean importRecommendations() {
        try {
            // Call the external recommendation API
            String url = "http://localhost:8000/api/v1/interactions/train_and_recommend/";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("recommendations")) {
                List<Map<String, Object>> recommendations = (List<Map<String, Object>>) response.get("recommendations");
                
                for (Map<String, Object> userRec : recommendations) {
                    String userId = (String) userRec.get("user_id");
                    List<Map<String, Object>> hybridRecs = (List<Map<String, Object>>) userRec.get("hybrid_recommendations");
                    
                    List<Recommendation.RecommendedPost> recommendedPosts = new ArrayList<>();
                    
                    for (Map<String, Object> rec : hybridRecs) {
                        String postId = (String) rec.get("post_id");
                        double score = ((Number) rec.get("score")).doubleValue();
                        
                        Recommendation.RecommendedPost post = Recommendation.RecommendedPost.builder()
                                .postId(postId)
                                .score(score)
                                .build();
                                
                        recommendedPosts.add(post);
                    }
                    
                    // Save the recommendations
                    recommendationService.saveRecommendations(userId, recommendedPosts);
                }
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 