package vn.iuh.ktpm.eduforgepost.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "series")
public class Series {
    
    @Id
    private String id;
    
    @Indexed
    private UUID userId;  // Reference to the user in the user service
    
    private String title;
    
    private String description;
    
    private String coverImage;  // URL to the cover image
    
    @Builder.Default
    private List<SeriesItem> posts = new ArrayList<>();
    
    @Builder.Default
    private boolean isPublished = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * Represents a post in a series with its order
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesItem {
        private String postId;  // Reference to the Post
        private int order;      // Order of the post in the series
    }
}
