package vn.iuh.ktpm.eduforgepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iuh.ktpm.eduforgepost.dto.PostLikeRequest;
import vn.iuh.ktpm.eduforgepost.exception.ResourceNotFoundException;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.PostLike;
import vn.iuh.ktpm.eduforgepost.repository.PostLikeRepository;
import vn.iuh.ktpm.eduforgepost.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    
    @Transactional
    public boolean toggleLike(PostLikeRequest request) {
        String postId = request.getPostId();
        String userId = request.getUserId();
        
        // Check if post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        // Check if user already liked the post
        boolean alreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        
        if (alreadyLiked) {
            // Unlike the post
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            
            // Update like count in post
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            
            return false; // Indicates the post is now unliked
        } else {
            // Like the post
            PostLike postLike = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .likedAt(LocalDateTime.now())
                    .build();
            
            postLikeRepository.save(postLike);
            
            // Update like count in post
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            
            return true; // Indicates the post is now liked
        }
    }
    
    public List<PostLike> getLikesByPostId(String postId) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post", "id", postId);
        }
        
        return postLikeRepository.findByPostId(postId);
    }
    
    public List<PostLike> getLikesByUserId(String userId) {
        return postLikeRepository.findByUserId(userId);
    }
    
    public boolean checkIfUserLikedPost(String postId, String userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
