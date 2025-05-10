package vn.iuh.ktpm.eduforgepost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import vn.iuh.ktpm.eduforgepost.model.Recommendation;

import java.util.Optional;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    
    /**
     * Find recommendation by user ID
     * 
     * @param userId the user ID
     * @return optional recommendation
     */
    Optional<Recommendation> findByUserId(String userId);
} 