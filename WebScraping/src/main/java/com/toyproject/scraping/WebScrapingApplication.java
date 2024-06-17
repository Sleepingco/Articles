package com.toyproject.scraping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WebScrapingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebScrapingApplication.class, args);
	}

}
