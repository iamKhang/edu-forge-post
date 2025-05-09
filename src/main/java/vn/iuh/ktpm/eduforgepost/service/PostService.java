package vn.iuh.ktpm.eduforgepost.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.iuh.ktpm.eduforgepost.dto.PostRequest;
import vn.iuh.ktpm.eduforgepost.dto.PostResponse;
import vn.iuh.ktpm.eduforgepost.exception.ResourceNotFoundException;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.Series;
import vn.iuh.ktpm.eduforgepost.repository.PostLikeRepository;
import vn.iuh.ktpm.eduforgepost.repository.PostRepository;
import vn.iuh.ktpm.eduforgepost.repository.SeriesRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public PostResponse createPost(PostRequest postRequest) {
        Post post = Post.builder()
                .userId(postRequest.getUserId())
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .coverImage(postRequest.getCoverImage())
                .tags(postRequest.getTags())
                .seriesId(postRequest.getSeriesId())
                .isPublished(postRequest.isPublished())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);

        // If post is part of a series, update the series
        if (postRequest.getSeriesId() != null) {
            Optional<Series> seriesOpt = seriesRepository.findById(postRequest.getSeriesId());
            if (seriesOpt.isPresent()) {
                Series series = seriesOpt.get();
                
                // Get the current max order in the series
                int maxOrder = series.getPosts().stream()
                        .mapToInt(Series.SeriesItem::getOrder)
                        .max()
                        .orElse(0);

                // Add the new post to the series
                series.getPosts().add(Series.SeriesItem.builder()
                        .postId(savedPost.getId())
                        .order(maxOrder + 1)
                        .build());

                seriesRepository.save(series);
            }
        }

        return mapToPostResponse(savedPost, postRequest.getUserId());
    }

    public PostResponse getPostById(String id, String currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return mapToPostResponse(post, currentUserId);
    }

    public Page<PostResponse> getAllPosts(Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByIsPublishedTrue(pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getPostsByUserId(String userId, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByUserId(userId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> searchPosts(String keyword, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword, keyword, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getPostsByTag(String tag, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByTagsContaining(tag, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getPostsBySeriesId(String seriesId, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findBySeriesId(seriesId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getPostsWithoutSeries(Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findBySeriesIdIsNull(pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getUserPostsWithoutSeries(String userId, Pageable pageable, String currentUserId) {
        Page<Post> posts = postRepository.findByUserIdAndSeriesIdIsNull(userId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    @Transactional
    public PostResponse updatePost(String id, PostRequest postRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // Verify that the user is the owner of the post
        if (!post.getUserId().equals(postRequest.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to update this post");
        }

        // Check if series ID is changing
        String oldSeriesId = post.getSeriesId();
        String newSeriesId = postRequest.getSeriesId();

        // If series ID is changing, update the series
        if ((oldSeriesId != null && !oldSeriesId.equals(newSeriesId)) ||
            (oldSeriesId == null && newSeriesId != null)) {

            // If post was in a series before, remove it from that series
            if (oldSeriesId != null) {
                Optional<Series> oldSeries = seriesRepository.findById(oldSeriesId);
                if (oldSeries.isPresent()) {
                    Series series = oldSeries.get();
                    series.setPosts(series.getPosts().stream()
                            .filter(item -> !item.getPostId().equals(id))
                            .collect(Collectors.toList()));
                    seriesRepository.save(series);
                }
            }

            // If post is being added to a series, add it to that series
            if (newSeriesId != null) {
                Optional<Series> newSeries = seriesRepository.findById(newSeriesId);
                if (newSeries.isPresent()) {
                    Series series = newSeries.get();

                    // Check if post is already in the series
                    boolean postAlreadyInSeries = series.getPosts().stream()
                            .anyMatch(item -> item.getPostId().equals(id));

                    if (!postAlreadyInSeries) {
                        // Add post to the end of the series
                        int maxOrder = series.getPosts().stream()
                                .mapToInt(Series.SeriesItem::getOrder)
                                .max()
                                .orElse(0);

                        series.getPosts().add(Series.SeriesItem.builder()
                                .postId(id)
                                .order(maxOrder + 1)
                                .build());

                        seriesRepository.save(series);
                    }
                }
            }
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setCoverImage(postRequest.getCoverImage());
        post.setTags(postRequest.getTags());
        post.setSeriesId(postRequest.getSeriesId());
        post.setPublished(postRequest.isPublished());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost, postRequest.getUserId());
    }

    @Transactional
    public void deletePost(String id, String userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // Verify that the user is the owner of the post
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this post");
        }

        // If post is part of a series, remove it from the series
        if (post.getSeriesId() != null) {
            Optional<Series> seriesOpt = seriesRepository.findById(post.getSeriesId());
            if (seriesOpt.isPresent()) {
                Series series = seriesOpt.get();
                series.setPosts(series.getPosts().stream()
                        .filter(item -> !item.getPostId().equals(id))
                        .collect(Collectors.toList()));
                seriesRepository.save(series);
            }
        }

        postRepository.delete(post);
    }

    private PostResponse mapToPostResponse(Post post, String currentUserId) {
        boolean likedByCurrentUser = postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);

        // Get series information if post is part of a series
        String seriesTitle = null;
        Integer orderInSeries = null;

        if (post.getSeriesId() != null) {
            Optional<Series> seriesOpt = seriesRepository.findById(post.getSeriesId());
            if (seriesOpt.isPresent()) {
                Series series = seriesOpt.get();
                seriesTitle = series.getTitle();

                // Find the order of the post in the series
                for (Series.SeriesItem item : series.getPosts()) {
                    if (item.getPostId().equals(post.getId())) {
                        orderInSeries = item.getOrder();
                        break;
                    }
                }
            }
        }

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .coverImage(post.getCoverImage())
                .tags(post.getTags())
                .likeCount(post.getLikeCount())
                .isPublished(post.isPublished())
                .seriesId(post.getSeriesId())
                .seriesTitle(seriesTitle)
                .orderInSeries(orderInSeries)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likedByCurrentUser(likedByCurrentUser)
                .build();
    }
}
