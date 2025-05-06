# Edu Forge Post Service

Microservice for managing posts, series, and post interactions in the Edu Forge platform.

## Features

- Post management (create, read, update, delete)
- Series management (organize posts into series with ordering)
- Post likes tracking
- Cover image support for posts and series

## Technology Stack

- Java 21
- Spring Boot 3.4.5
- MongoDB
- Maven

## Setup and Configuration

### Local Development

1. Clone the repository
2. Make sure you have MongoDB running locally on port 27017
3. The application is configured to use the `dev` profile by default, which connects to a local MongoDB instance

### Production Setup

1. Copy `.env.example` to `.env` and fill in your MongoDB Atlas credentials
2. Copy `src/main/resources/application-prod.properties.example` to `src/main/resources/application-prod.properties`
3. Update `application.properties` to use the `prod` profile:
   ```properties
   spring.profiles.active=prod
   ```

### Environment Variables

For production deployment, set the following environment variables:

- `MONGODB_USERNAME`: Your MongoDB Atlas username
- `MONGODB_PASSWORD`: Your MongoDB Atlas password
- `MONGODB_CLUSTER`: Your MongoDB Atlas cluster address
- `MONGODB_DATABASE`: Your database name

## API Documentation

### Posts API

- `POST /api/posts`: Create a new post
- `GET /api/posts/{id}`: Get a post by ID
- `GET /api/posts`: Get all posts (with pagination and sorting)
- `GET /api/posts/user/{userId}`: Get posts by user ID
- `GET /api/posts/search?keyword=`: Search posts by keyword
- `GET /api/posts/tag/{tag}`: Get posts by tag
- `GET /api/posts/series/{seriesId}`: Get posts in a series
- `GET /api/posts/no-series`: Get posts not in any series
- `PUT /api/posts/{id}`: Update a post
- `DELETE /api/posts/{id}?userId=`: Delete a post

### Series API

- `POST /api/series`: Create a new series
- `GET /api/series/{id}`: Get a series by ID
- `GET /api/series`: Get all series (with pagination and sorting)
- `GET /api/series/user/{userId}`: Get series by user ID
- `GET /api/series/search?keyword=`: Search series by keyword
- `PUT /api/series/{id}`: Update a series
- `DELETE /api/series/{id}?userId=`: Delete a series
- `POST /api/series/{seriesId}/posts/{postId}`: Add a post to a series
- `DELETE /api/series/{seriesId}/posts/{postId}`: Remove a post from a series
- `PUT /api/series/{seriesId}/posts/{postId}/order`: Update post order in a series

### Post Likes API

- `POST /api/post-likes/toggle`: Toggle like/unlike on a post
- `GET /api/post-likes/post/{postId}`: Get all likes for a post
- `GET /api/post-likes/user/{userId}`: Get all posts liked by a user
- `GET /api/post-likes/check?postId=&userId=`: Check if a user liked a post

## Development Guidelines

- Use appropriate HTTP methods for API endpoints
- Follow RESTful API design principles
- Add validation for request payloads
- Handle exceptions properly
- Write unit tests for services and controllers

## License

[Your License Information]
