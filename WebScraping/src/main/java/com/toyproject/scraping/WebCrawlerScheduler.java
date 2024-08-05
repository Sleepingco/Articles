package com.toyproject.scraping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebCrawlerScheduler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerScheduler.class);

    @Autowired
    private ArticleScrapService articleScrapService;

    @Autowired
    private SeleniumLinkedin seleniumLinkedin;

    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
    public void executeTask() {
        logger.info("Task executed at 4 AM every day");
        try {
            articleScrapService.jojolduCrawlAndSaveArticles();
            seleniumLinkedin.ScrapLinkedinSelenium();
        } catch (Exception e) {
            logger.error("Error during scheduled tasks", e);
        }
    }
}
