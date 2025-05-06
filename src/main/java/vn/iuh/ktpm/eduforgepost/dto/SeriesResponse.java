package vn.iuh.ktpm.eduforgepost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesResponse {
    
    private String id;
    private UUID userId;
    private String title;
    private String description;
    private String coverImage;
    private List<SeriesItemDto> posts = new ArrayList<>();
    private boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesItemDto {
        private String postId;
        private String postTitle;
        private String postCoverImage;
        private int order;
    }
}
