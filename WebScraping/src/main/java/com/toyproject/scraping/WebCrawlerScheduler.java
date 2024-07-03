package com.toyproject.scraping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class WebCrawlerScheduler {
	@Autowired
	private ArticleScrapService articleScrapService;
	@Scheduled(cron = "0 0 4 * * ?")
    public void executeTask() {
        System.out.println("Task executed at 4 AM every day");
        // 여기에 수행할 작업 로직 추가
        articleScrapService.jojolduCrawlAndSaveArticles();
    }
}
