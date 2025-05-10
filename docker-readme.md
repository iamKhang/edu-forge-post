# Docker Deployment for EduForge Post Service

This document provides instructions for deploying the EduForge Post Service using Docker and Docker Compose.

## Prerequisites

- Docker Engine (version 20.10.0 or higher)
- Docker Compose (version 2.0.0 or higher)

## Configuration

### Environment Variables

The application uses the following environment variables which can be modified in the `docker-compose.yml` file:

- `SPRING_DATA_MONGODB_URI`: MongoDB connection string
- `SPRING_DATA_MONGODB_AUTO_INDEX_CREATION`: Enable/disable automatic index creation

## Deployment

### Build and Start Services

To build and start all services (application, MongoDB, and recommendation service):

```bash
docker-compose up -d
```

This command builds the application image using the Dockerfile and starts all services defined in the docker-compose.yml file.

### View Logs

To view logs from the application:

```bash
docker-compose logs -f app
```

### Stop Services

To stop all services:

```bash
docker-compose down
```

To stop all services and remove volumes:

```bash
docker-compose down -v
```

## Services

The deployment includes the following services:

1. **app**: The EduForge Post Service (Spring Boot application)
   - Accessible at http://localhost:8080

2. **mongodb**: MongoDB database for storing post data
   - Port: 27017

3. **recommendation-service**: Service for post recommendations
   - Accessible at http://localhost:8000

## Data Persistence

MongoDB data is persisted using Docker volumes (`mongodb-data`). This ensures that your data survives container restarts and removals.

## Customization

To customize the deployment, you can:

1. Modify the `Dockerfile` to change how the application is built
2. Update the `docker-compose.yml` file to change service configurations
3. Add additional services as needed

## Scheduled Tasks

The application includes a scheduled task that runs at midnight (00:00:00) every day to import recommendations from the recommendation service. This happens automatically and requires no manual intervention. 