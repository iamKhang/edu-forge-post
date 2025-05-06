package vn.iuh.ktpm.eduforgepost.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration class.
 * This class is used to configure MongoDB connection based on active profile.
 * Most of the configuration is done in application-{profile}.properties files.
 * 
 * This class can be extended to provide additional MongoDB configuration if needed.
 */
@Configuration
@EnableMongoRepositories(basePackages = "vn.iuh.ktpm.eduforgepost.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        // This is a fallback value. The actual database name should be configured in properties files
        return "edu_forge_post_db";
    }
    
    // You can override more methods here if you need custom MongoDB configuration
    // For example, to customize the MongoClient settings
    
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
