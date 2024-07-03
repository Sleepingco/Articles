package com.toyproject.scraping;

import java.time.LocalDateTime;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class SeleniumLinkedin {
	
	@Autowired
	private ArticleDAO articleDAO;
	public SeleniumLinkedin(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	public void JojolduSeleniumLinkedin() {
		
		LocalDateTime now = LocalDateTime.now();
		urlclass myurl = new urlclass();
		String urljojoldu = myurl.getJojolduLinkedinUrl();
		
		// driver porperty and make new driver
		System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		WebDriver driver = new ChromeDriver(options);
		LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
		login.loginToLinkedIn();
		driver.get(urljojoldu);  // 무한 스크롤이 적용된 페이지 URL
		JavascriptExecutor js = (JavascriptExecutor) driver;
		while (true) {
			 // 현재 페이지의 높이를 저장
		    long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
		    // 페이지의 하단으로 스크롤
		    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		}
	}
	
}
