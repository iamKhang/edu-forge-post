package vn.iuh.ktpm.eduforgepost.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * MongoDB configuration class.
 * Reads configuration from environment variables or application properties.
 */
@Configuration
@EnableMongoRepositories(basePackages = "vn.iuh.ktpm.eduforgepost.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBConfig.class);

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private int port;

    @Value("${spring.data.mongodb.database:eduforge_post}")
    private String database;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String uri;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        if (uri != null && !uri.isEmpty()) {
            logger.info("Connecting to MongoDB using URI: {}", uri.replaceAll(":[^:@/]+@", ":****@"));
            return MongoClients.create(uri);
        }

        String connectionString = String.format("mongodb://%s:%d/%s", host, port, database);
        logger.info("Connecting to MongoDB at {}:{} with database {}", host, port, database);
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
