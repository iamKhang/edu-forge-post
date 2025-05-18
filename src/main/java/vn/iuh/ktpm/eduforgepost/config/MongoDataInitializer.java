package vn.iuh.ktpm.eduforgepost.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
                
                // Import collections
                importCollection("edu_forge_post_db.posts.json", "posts");
                importCollection("edu_forge_post_db.series.json", "series");
                importCollection("edu_forge_post_db.recommendations.json", "recommendations");

                log.info("Successfully imported initial data into MongoDB");
            } catch (Exception e) {
                log.error("Error importing initial data: ", e);
            }
        };
    }

    private void importCollection(String jsonFile, String collectionName) {
        try {
            log.info("Checking collection '{}' for importing data from '{}'", collectionName, jsonFile);
            
            // Verify resource file exists
            ClassPathResource resource = new ClassPathResource(jsonFile);
            if (!resource.exists()) {
                log.error("Resource file '{}' does not exist!", jsonFile);
                return;
            }
            
            // Get collection and check if empty
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            long count = collection.countDocuments();
            log.info("Collection '{}' current document count: {}", collectionName, count);
            
            if (count == 0 || forceImport) {
                // Drop existing collection if force import is enabled
                if (forceImport && count > 0) {
                    log.info("Force import enabled. Dropping existing collection '{}'", collectionName);
                    mongoTemplate.dropCollection(collectionName);
                }
                
                // Read JSON content directly as string
                String jsonContent;
                try (InputStream is = resource.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    jsonContent = sb.toString();
                }
                
                // Parse JSON array into list of documents
                List<Document> documents = new ArrayList<>();
                
                // Remove outer array brackets and split by document
                String content = jsonContent.trim();
                if (content.startsWith("[") && content.endsWith("]")) {
                    content = content.substring(1, content.length() - 1);
                }
                
                // Handle empty array case
                if (content.trim().isEmpty()) {
                    log.info("JSON file '{}' contains an empty array", jsonFile);
                    return;
                }
                
                // Parse each document in the array
                int openBraces = 0;
                int startIndex = 0;
                boolean inQuotes = false;
                boolean escaped = false;
                
                for (int i = 0; i < content.length(); i++) {
                    char c = content.charAt(i);
                    
                    if (escaped) {
                        escaped = false;
                        continue;
                    }
                    
                    if (c == '\\') {
                        escaped = true;
                        continue;
                    }
                    
                    if (c == '"') {
                        inQuotes = !inQuotes;
                        continue;
                    }
                    
                    if (inQuotes) {
                        continue;
                    }
                    
                    if (c == '{') {
                        if (openBraces == 0) {
                            startIndex = i;
                        }
                        openBraces++;
                    } else if (c == '}') {
                        openBraces--;
                        if (openBraces == 0) {
                            String documentJson = content.substring(startIndex, i + 1);
                            documents.add(Document.parse(documentJson));
                            
                            // Skip comma and whitespace
                            while (i + 1 < content.length() && (content.charAt(i + 1) == ',' || Character.isWhitespace(content.charAt(i + 1)))) {
                                i++;
                            }
                        }
                    }
                }
                
                // Insert documents into collection
                if (!documents.isEmpty()) {
                    collection.insertMany(documents, new InsertManyOptions().ordered(false));
                    log.info("Imported {} documents into '{}' collection", documents.size(), collectionName);
                } else {
                    log.warn("No documents were parsed from '{}'", jsonFile);
                }
            } else {
                log.info("Collection '{}' already contains {} documents, skipping import", collectionName, count);
            }
        } catch (Exception e) {
            log.error("Error importing data from '{}' to collection '{}': {}", jsonFile, collectionName, e.getMessage(), e);
        }
    }
} 