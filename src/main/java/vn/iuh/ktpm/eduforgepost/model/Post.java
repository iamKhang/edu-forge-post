package vn.iuh.ktpm.eduforgepost.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @Indexed
    private String userId;  // Reference to the user in the user service

    private String title;

    private String content;

    private String coverImage;  // URL to the cover image

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private List<PostInteraction> likes = new ArrayList<>();

    @Builder.Default
    private List<PostInteraction> views = new ArrayList<>();

    @Builder.Default
    private boolean isPublished = true;

    // Series information (optional)
    private String seriesId;  // Reference to the Series (null if not part of any series)

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInteraction {
        private String userId;
        private LocalDateTime timestamp;
    }
}
