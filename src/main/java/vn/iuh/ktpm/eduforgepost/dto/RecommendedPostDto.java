package vn.iuh.ktpm.eduforgepost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPostDto {
    private String postId;
    private String title;
    private String coverImage;
    private String content;
    private double score;
} 