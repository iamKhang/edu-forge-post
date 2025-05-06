package vn.iuh.ktpm.eduforgepost.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iuh.ktpm.eduforgepost.model.PostLike;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends MongoRepository<PostLike, String> {
    
    List<PostLike> findByPostId(String postId);
    
    List<PostLike> findByUserId(UUID userId);
    
    Optional<PostLike> findByPostIdAndUserId(String postId, UUID userId);
    
    boolean existsByPostIdAndUserId(String postId, UUID userId);
    
    long countByPostId(String postId);
    
    void deleteByPostIdAndUserId(String postId, UUID userId);
}
