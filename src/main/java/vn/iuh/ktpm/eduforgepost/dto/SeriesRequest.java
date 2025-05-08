package vn.iuh.ktpm.eduforgepost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesRequest {
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private String coverImage;
    
    @Builder.Default
    private List<SeriesItemDto> posts = new ArrayList<>();
    
    @Builder.Default
    private boolean isPublished = true;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesItemDto {
        private String postId;
        private int order;
    }
}
