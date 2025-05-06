package vn.iuh.ktpm.eduforgepost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class EduForgePostApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduForgePostApplication.class, args);
    }

}
