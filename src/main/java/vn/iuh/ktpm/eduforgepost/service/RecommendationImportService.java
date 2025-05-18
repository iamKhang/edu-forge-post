package vn.iuh.ktpm.eduforgepost.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.iuh.ktpm.eduforgepost.model.Recommendation;

@Service
public class RecommendationImportService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationImportService.class);

    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${recommendation.service.url:https://eduforge.io.vn:8090}")
    private String recommendationServiceUrl;
    
    /**
     * Fetch recommendations from external API and save to database
     * 
     * @return true if successful, false otherwise
     */
    public boolean importRecommendations() {
        try {
            // Call the external recommendation API
            String url = recommendationServiceUrl + "/api/v1/interactions/train_and_recommend/";
            logger.info("Calling recommendation API at: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("recommendations")) {
                List<Map<String, Object>> recommendations = (List<Map<String, Object>>) response.get("recommendations");
                logger.info("Received {} user recommendations", recommendations.size());
                
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
                    logger.info("Saved {} recommendations for user {}", recommendedPosts.size(), userId);
                }
                
                return true;
            } else {
                logger.error("Invalid response format, missing 'recommendations' field");
                return false;
            }
        } catch (Exception e) {
            logger.error("Error importing recommendations: ", e);
            return false;
        }
    }
} 