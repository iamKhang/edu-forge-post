package vn.iuh.ktpm.eduforgepost.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Custom deserializer for MongoDB date objects to LocalDateTime
 */
public class MongoDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        // Handle MongoDB date format: {"$date": "2023-01-01T00:00:00Z"}
        if (node.isObject() && node.has("$date")) {
            JsonNode dateNode = node.get("$date");
            if (dateNode.isTextual()) {
                // Parse ISO date string
                return LocalDateTime.parse(dateNode.asText().replace("Z", ""));
            } else if (dateNode.isNumber()) {
                // Parse timestamp (in milliseconds)
                long timestamp = dateNode.asLong();
                return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), 
                    ZoneId.systemDefault()
                );
            }
        }
        
        // Handle string format (ISO)
        if (node.isTextual()) {
            String text = node.asText();
            if (text.endsWith("Z")) {
                text = text.replace("Z", "");
            }
            return LocalDateTime.parse(text);
        }
        
        throw new IOException("Cannot deserialize LocalDateTime from " + node);
    }
} 