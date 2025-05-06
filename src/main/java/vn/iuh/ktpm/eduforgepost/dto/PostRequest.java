package vn.iuh.ktpm.eduforgepost.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String coverImage;  // URL to the cover image

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // Optional series ID if the post belongs to a series
    private String seriesId;

    @Builder.Default
    private boolean isPublished = true;
}
