package vn.iuh.ktpm.eduforgepost.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import vn.iuh.ktpm.eduforgepost.model.Post;
import vn.iuh.ktpm.eduforgepost.model.Recommendation;
import vn.iuh.ktpm.eduforgepost.model.Series;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoDataInitializer {

    private final MongoTemplate mongoTemplate;
    
    @Value("${spring.data.mongodb.force-import:false}")
    private boolean forceImport;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            try {
                log.info("Starting data initialization... Force import: {}", forceImport);
                
                // Log MongoDB connection details
                log.info("MongoDB Database: {}", mongoTemplate.getDb().getName());
                
                // Configure ObjectMapper with JavaTimeModule for handling dates
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                // Configure ObjectMapper to ignore unknown properties
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                // Import posts
                importData("edu_forge_post_db.posts.json", Post[].class, "posts");

                // Import series
                importData("edu_forge_post_db.series.json", Series[].class, "series");

                // Import recommendations
                importData("edu_forge_post_db.recommendations.json", Recommendation[].class, "recommendations");

                log.info("Successfully imported initial data into MongoDB");
            } catch (Exception e) {
                log.error("Error importing initial data: ", e);
            }
        };
    }

    private <T> void importData(String jsonFile, Class<T[]> clazz, String collectionName) throws IOException {
        log.info("Checking collection '{}' for importing data from '{}'", collectionName, jsonFile);
        
        // Log if file exists in resources
        ClassPathResource resource = new ClassPathResource(jsonFile);
        boolean fileExists = resource.exists();
        log.info("Resource file '{}' exists: {}", jsonFile, fileExists);
        
        // First, check if collection is empty or force import is enabled
        long count = mongoTemplate.getCollection(collectionName).countDocuments();
        log.info("Collection '{}' current document count: {}", collectionName, count);
        
        if (count == 0 || forceImport) {
            if (forceImport && count > 0) {
                log.info("Force import enabled. Dropping existing collection '{}'", collectionName);
                mongoTemplate.dropCollection(collectionName);
            }
            
            log.info("Importing data into collection '{}'", collectionName);
            try (InputStream inputStream = resource.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                // Configure ObjectMapper to ignore unknown properties like "_id"
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                T[] data = objectMapper.readValue(inputStream, clazz);
                log.info("Successfully parsed {} documents from '{}'", data.length, jsonFile);
                
                // Insert all documents
                Arrays.stream(data).forEach(item -> mongoTemplate.save(item, collectionName));
                
                log.info("Imported {} documents into '{}' collection", data.length, collectionName);
            } catch (Exception e) {
                log.error("Error importing data from '{}' to collection '{}': ", jsonFile, collectionName, e);
            }
        } else {
            log.info("Collection '{}' already contains {} documents, skipping import", collectionName, count);
        }
    }
} 