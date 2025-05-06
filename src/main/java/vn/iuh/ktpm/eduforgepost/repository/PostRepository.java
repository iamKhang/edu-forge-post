package vn.iuh.ktpm.eduforgepost.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import vn.iuh.ktpm.eduforgepost.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByIsPublishedTrue(Pageable pageable);

    Page<Post> findByUserId(UUID userId, Pageable pageable);

    List<Post> findByUserIdAndIsPublishedTrue(UUID userId);

    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable);

    Page<Post> findByTagsContaining(String tag, Pageable pageable);

    // Series related queries
    List<Post> findBySeriesId(String seriesId);

    Page<Post> findBySeriesId(String seriesId, Pageable pageable);

    List<Post> findBySeriesIdAndIsPublishedTrue(String seriesId);

    // Find posts that are not part of any series
    Page<Post> findBySeriesIdIsNull(Pageable pageable);

    // Find posts that are not part of any series for a specific user
    Page<Post> findByUserIdAndSeriesIdIsNull(UUID userId, Pageable pageable);
}
