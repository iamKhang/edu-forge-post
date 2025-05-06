package vn.iuh.ktpm.eduforgepost.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_likes")
@CompoundIndex(name = "post_user_idx", def = "{'postId': 1, 'userId': 1}", unique = true)
public class PostLike {
    
    @Id
    private String id;
    
    @Indexed
    private String postId;  // Reference to the Post
    
    @Indexed
    private UUID userId;  // Reference to the user in the user service
    
    @CreatedDate
    private LocalDateTime likedAt;
}
