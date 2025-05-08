package vn.iuh.ktpm.eduforgepost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeRequest {
    
    @NotBlank(message = "Post ID is required")
    private String postId;
    
    @NotNull(message = "User ID is required")
    private String userId;
}
