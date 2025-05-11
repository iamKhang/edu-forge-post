package vn.iuh.ktpm.eduforgepost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesResponse {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String coverImage;
    private List<SeriesItemResponse> posts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesItemResponse {
        private String postId;
        private String postTitle;
        private String postCoverImage;
        private String content;
        private int order;
        private long totalLikes;
        private long totalViews;
        private boolean likedByCurrentUser;
        private boolean viewedByCurrentUser;
    }
}
