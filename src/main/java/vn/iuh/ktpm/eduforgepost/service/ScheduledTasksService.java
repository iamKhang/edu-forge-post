package vn.iuh.ktpm.eduforgepost.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksService.class);
    
    @Autowired
    private RecommendationImportService recommendationImportService;
    
    /**
     * Scheduled task to import recommendations at midnight (00:00:00) every day
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledRecommendationImport() {
        logger.info("Starting scheduled recommendation import at midnight");
        boolean success = recommendationImportService.importRecommendations();
        logger.info("Scheduled recommendation import completed with status: {}", success);
    }
} 