package vn.iuh.ktpm.eduforgepost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iuh.ktpm.eduforgepost.model.Series;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeriesRepository extends MongoRepository<Series, String> {
    
    Page<Series> findByIsPublishedTrue(Pageable pageable);
    
    Page<Series> findByUserId(String userId, Pageable pageable);
    
    List<Series> findByUserIdAndIsPublishedTrue(String userId);
    
    Page<Series> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword, String descriptionKeyword, Pageable pageable);

    long countByIsPublishedTrue();
    
    long countByIsPublishedFalse();
}
