package vn.iuh.ktpm.eduforgepost.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private String id;
    private String userId;
    private String title;
    private String content;
    private String coverImage;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    // Total counts
    private long totalLikes;
    private long totalViews;
    
    // User interaction status
    private boolean likedByCurrentUser;
    private boolean viewedByCurrentUser;
    
    private boolean isPublished;

    // Series information
    private String seriesId;
    private String seriesTitle;
    private Integer orderInSeries;  // Null if not part of a series

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInteractionResponse {
        private String userId;
        private LocalDateTime timestamp;
    }
}
