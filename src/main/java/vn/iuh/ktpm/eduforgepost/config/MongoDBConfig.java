package vn.iuh.ktpm.eduforgepost.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * MongoDB configuration class.
 * Reads configuration from environment variables or application properties.
 */
@Configuration
@EnableMongoRepositories(basePackages = "vn.iuh.ktpm.eduforgepost.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;
    
    @Value("${spring.data.mongodb.port:27017}")
    private int port;
    
    @Value("${spring.data.mongodb.database:eduforge_post}")
    private String database;
    
    @Value("${spring.data.mongodb.username:}")
    private String username;
    
    @Value("${spring.data.mongodb.password:}")
    private String password;
    
    @Value("${spring.data.mongodb.uri:#{null}}")
    private String uri;

    @Override
    protected String getDatabaseName() {
        return database;
    }
    
    @Override
    public MongoClient mongoClient() {
        if (uri != null && !uri.isEmpty()) {
            return MongoClients.create(uri);
        }
        
        // Thực hiện URL encode username và password
        String encodedUsername = username;
        String encodedPassword = password;
        
        try {
            if (username != null && !username.isEmpty()) {
                encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            }
            if (password != null && !password.isEmpty()) {
                encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error URL encoding MongoDB credentials", e);
        }
        
        // Tạo connection string với username và password đã encode
        String connectionString;
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=admin", 
                    encodedUsername, encodedPassword, host, port, database);
        } else {
            connectionString = String.format("mongodb://%s:%d/%s", host, port, database);
        }
        
        return MongoClients.create(connectionString);
    }
    
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
