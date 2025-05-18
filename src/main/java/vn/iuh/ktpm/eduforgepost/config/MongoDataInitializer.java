package vn.iuh.ktpm.eduforgepost.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            try {
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
        // First, check if collection is empty
        if (mongoTemplate.getCollection(collectionName).countDocuments() == 0) {
            // Read JSON file from resources
            ClassPathResource resource = new ClassPathResource(jsonFile);
            try (InputStream inputStream = resource.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                
                T[] data = objectMapper.readValue(inputStream, clazz);
                
                // Insert all documents
                Arrays.stream(data).forEach(item -> mongoTemplate.save(item, collectionName));
                
                log.info("Imported {} documents into {} collection", data.length, collectionName);
            }
        } else {
            log.info("Collection {} already contains data, skipping import", collectionName);
        }
    }
} 