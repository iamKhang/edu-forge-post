# Post API Guide

## Endpoints

### 1. Get Posts by Series ID
```http
GET /api/posts/series/{seriesId}
```

**Path Parameters:**
- `seriesId` (string, required): ID of the series to get posts from

**Query Parameters:**
- `page` (integer, default: 0): Page number (0-based)
- `size` (integer, default: 10): Number of items per page
- `sortBy` (string, default: "createdAt"): Field to sort by (e.g., "createdAt", "title", "totalLikes")
- `sortDir` (string, default: "desc"): Sort direction ("asc" or "desc")
- `currentUserId` (string, optional): ID of the current user to check like/view status

**Response:**
```json
{
    "success": true,
    "message": null,
    "data": {
        "content": [
            {
                "id": "string",
                "userId": "string",
                "title": "string",
                "content": "string",
                "coverImage": "string",
                "tags": ["string"],
                "totalLikes": 0,
                "totalViews": 0,
                "likedByCurrentUser": false,
                "viewedByCurrentUser": false,
                "isPublished": true,
                "seriesId": "string",
                "seriesTitle": "string",
                "orderInSeries": 0,
                "createdAt": "2024-03-21T10:00:00",
                "updatedAt": "2024-03-21T10:00:00"
            }
        ],
        "pageable": {
            "pageNumber": 0,
            "pageSize": 10,
            "sort": {
                "sorted": true,
                "unsorted": false,
                "empty": false
            }
        },
        "totalElements": 0,
        "totalPages": 0,
        "last": true,
        "first": true,
        "empty": false
    }
}
```

### 2. Get Posts Without Series
```http
GET /api/posts/no-series
```

**Query Parameters:** Same as Get Posts by Series ID

**Response:** Same as Get Posts by Series ID

### 3. Get User's Posts Without Series
```http
GET /api/posts/user/{userId}/no-series
```

**Path Parameters:**
- `userId` (string, required): ID of the user to get posts from

**Query Parameters:** Same as Get Posts by Series ID

**Response:** Same as Get Posts by Series ID

### 4. Create Post
```http
POST /api/posts
```

**Request Body:**
```json
{
    "userId": "string",      // Required: ID of the user creating the post
    "title": "string",       // Required: Title of the post
    "content": "string",     // Required: Content of the post
    "coverImage": "string",  // Optional: URL of the cover image
    "tags": ["string"],      // Optional: Array of tags
    "seriesId": "string",    // Optional: ID of the series this post belongs to
    "isPublished": true      // Optional: Whether the post is published (default: true)
}
```

**Response:**
```json
{
    "success": true,
    "message": "Post created successfully",
    "data": {
        "id": "string",
        "userId": "string",
        "title": "string",
        "content": "string",
        "coverImage": "string",
        "tags": ["string"],
        "totalLikes": 0,
        "totalViews": 0,
        "likedByCurrentUser": false,
        "viewedByCurrentUser": false,
        "isPublished": true,
        "seriesId": "string",
        "seriesTitle": "string",
        "orderInSeries": 0,
        "createdAt": "2024-03-21T10:00:00",
        "updatedAt": "2024-03-21T10:00:00"
    }
}
```

### 5. Get Post by ID
```http
GET /api/posts/{id}
```

**Path Parameters:**
- `id` (string, required): ID of the post to retrieve

**Query Parameters:**
- `currentUserId` (string, optional): ID of the current user to check like/view status

**Response:**
```json
{
    "success": true,
    "message": null,
    "data": {
        "id": "string",
        "userId": "string",
        "title": "string",
        "content": "string",
        "coverImage": "string",
        "tags": ["string"],
        "totalLikes": 0,
        "totalViews": 0,
        "likedByCurrentUser": false,
        "viewedByCurrentUser": false,
        "isPublished": true,
        "seriesId": "string",
        "seriesTitle": "string",
        "orderInSeries": 0,
        "createdAt": "2024-03-21T10:00:00",
        "updatedAt": "2024-03-21T10:00:00"
    }
}
```

### 6. Get All Posts
```http
GET /api/posts
```

**Query Parameters:** Same as Get Posts by Series ID

**Response:** Same as Get Posts by Series ID

### 7. Get Posts by User ID
```http
GET /api/posts/user/{userId}
```

**Path Parameters:**
- `userId` (string, required): ID of the user to get posts from

**Query Parameters:** Same as Get Posts by Series ID

**Response:** Same as Get Posts by Series ID

### 8. Search Posts
```http
GET /api/posts/search
```

**Query Parameters:**
- `keyword` (string, required): Search keyword to find in title or content
- `page` (integer, default: 0): Page number
- `size` (integer, default: 10): Number of items per page
- `currentUserId` (string, optional): ID of the current user to check like/view status

**Response:** Same as Get Posts by Series ID

### 9. Get Posts by Tag
```http
GET /api/posts/tag/{tag}
```

**Path Parameters:**
- `tag` (string, required): Tag to search for

**Query Parameters:**
- `page` (integer, default: 0): Page number
- `size` (integer, default: 10): Number of items per page
- `currentUserId` (string, optional): ID of the current user to check like/view status

**Response:** Same as Get Posts by Series ID

### 10. Update Post
```http
PUT /api/posts/{id}
```

**Path Parameters:**
- `id` (string, required): ID of the post to update

**Request Body:** Same as Create Post

**Response:**
```json
{
    "success": true,
    "message": "Post updated successfully",
    "data": {
        "id": "string",
        "userId": "string",
        "title": "string",
        "content": "string",
        "coverImage": "string",
        "tags": ["string"],
        "totalLikes": 0,
        "totalViews": 0,
        "likedByCurrentUser": false,
        "viewedByCurrentUser": false,
        "isPublished": true,
        "seriesId": "string",
        "seriesTitle": "string",
        "orderInSeries": 0,
        "createdAt": "2024-03-21T10:00:00",
        "updatedAt": "2024-03-21T10:00:00"
    }
}
```

### 11. Delete Post
```http
DELETE /api/posts/{id}
```

**Path Parameters:**
- `id` (string, required): ID of the post to delete

**Query Parameters:**
- `userId` (string, required): ID of the user (for authorization)

**Response:**
```json
{
    "success": true,
    "message": "Post deleted successfully",
    "data": null
}
```

### 12. Add View
```http
POST /api/posts/{id}/view
```

**Path Parameters:**
- `id` (string, required): ID of the post to add view

**Query Parameters:**
- `userId` (string, required): ID of the user viewing the post

**Description:**
- Adds a view to the post if the user hasn't viewed it before
- Each user can only view a post once
- The view is automatically recorded when a user views a post through the Get Post by ID endpoint

**Response:** Same as Get Post by ID

### 13. Toggle Like
```http
POST /api/posts/{id}/like
```

**Path Parameters:**
- `id` (string, required): ID of the post to toggle like

**Query Parameters:**
- `userId` (string, required): ID of the user liking/unliking the post

**Description:**
- Toggles the like status of a post for a specific user
- If the user hasn't liked the post, adds a like
- If the user has already liked the post, removes the like
- The response includes the updated like count and whether the current user has liked the post

**Response:** Same as Get Post by ID

## Notes
1. All endpoints return a standard response format:
   ```json
   {
       "success": boolean,
       "message": string | null,
       "data": any
   }
   ```

2. For paginated responses, the data includes:
   - `content`: Array of items
   - `pageable`: Pagination information
   - `totalElements`: Total number of items
   - `totalPages`: Total number of pages
   - `last`: Whether this is the last page
   - `first`: Whether this is the first page
   - `empty`: Whether the content is empty

3. For post responses, the data includes:
   - `totalLikes`: Total number of likes
   - `totalViews`: Total number of views
   - `likedByCurrentUser`: Whether the current user has liked the post
   - `viewedByCurrentUser`: Whether the current user has viewed the post

4. All timestamps are in ISO-8601 format (e.g., "2024-03-21T10:00:00")

5. Error Responses:
   ```json
   {
       "success": false,
       "message": "Error message",
       "data": null
   }
   ```

   Common error messages:
   - "Post not found"
   - "You are not authorized to update this post"
   - "You are not authorized to delete this post"
   - "Invalid request parameters" 