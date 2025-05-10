package vn.iuh.ktpm.eduforgepost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import vn.iuh.ktpm.eduforgepost.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    Page<Post> findByIsPublishedTrue(Pageable pageable);
    
    Page<Post> findByUserId(String userId, Pageable pageable);
    
    @Query("{'$or': [{'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?1, $options: 'i'}}]}")
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, Pageable pageable);
    
    Page<Post> findByTagsContaining(String tag, Pageable pageable);
    
    Page<Post> findBySeriesId(String seriesId, Pageable pageable);
    
    Page<Post> findBySeriesIdIsNull(Pageable pageable);
    
    Page<Post> findByUserIdAndSeriesIdIsNull(String userId, Pageable pageable);

    long countByIsPublishedTrue();
    
    long countByIsPublishedFalse();
    
    long countBySeriesIdIsNotNull();
    
    long countBySeriesIdIsNull();
    
    /**
     * Find all posts with IDs in the given list
     * 
     * @param ids list of post IDs
     * @return list of posts
     */
    List<Post> findAllByIdIn(List<String> ids);
}
