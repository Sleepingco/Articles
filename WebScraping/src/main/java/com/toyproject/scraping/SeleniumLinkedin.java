package com.toyproject.scraping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

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
		options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		
		WebDriver driver = new ChromeDriver(options);
		LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
		
		login.loginToLinkedIn();
		driver.get(urljojoldu);  // 무한 스크롤이 적용된 페이지 URL
//		JavascriptExecutor js = (JavascriptExecutor) driver;
//		
//		while (true) {
//			 // 현재 페이지의 높이를 저장
//		    long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
//		    
//		    // 페이지의 하단으로 스크롤
//		    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
//		    try {
//		        Thread.sleep(2000); 
//		    } catch (InterruptedException e) {
//		        Thread.currentThread().interrupt(); // 인터럽트 상태 복원
//		        System.err.println("스레드가 중단되었습니다: " + e.getMessage());
//		    }
//		    
//		    // 스크롤 후 페이지 높이를 확인
//		    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
//		    if (newHeight == lastHeight) {
//		        break; // 더 이상 로드되는 콘텐츠가 없으면 반복 종료
//		    }
//		}
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
		try {
			// 내용이 담겨있는 div 하나
		    List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
		    Collections.reverse(divs);
		    if (divs.isEmpty()) {
		        System.out.println("No divs found.");
		    } else {
		    	// 각요소 추출 글쓴이, 내용 등등..
		        for (WebElement div : divs) {
		        	WebElement author = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu > div > div > a.app-aware-link.update-components-actor__meta-link > span.update-components-actor__title > span.update-components-actor__name.hoverable-link-text.t-14.t-bold.t-black > span > span:nth-child(1)"));
		        	String content  = "";
		        	String originalPage = "";
		        	try {
		        		
		        		// 컨텐츠 와 내부에 링크 알아내기
		        		String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
		        		Document doc = Jsoup.parseBodyFragment(htmlContent);
		        		content = doc.text();
		        		// 컨텐츠 내부에 링크 박스를 알아내기
		        		content += " "+getLinkBoxSafe(div);
		        		
		        		// 원본 url 복사
		        		try {
		        			WebElement button = div.findElement(By.cssSelector(".feed-shared-control-menu__trigger.artdeco-button.artdeco-button--tertiary.artdeco-button--muted.artdeco-button--1.artdeco-button--circle.artdeco-dropdown__trigger.artdeco-dropdown__trigger--placement-bottom.ember-view"));
		                    wait.until(ExpectedConditions.elementToBeClickable(button)).click();
		        		    WebElement linkShare = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".feed-shared-control-menu__item.option-share-via")));
		        		    linkShare.click();
		        		    WebElement shareBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.artdeco-toast-item__cta")));
		        		    originalPage = shareBox.getAttribute("href");
		                    Thread.sleep(1000);
		                    WebElement closeBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".artdeco-toast-item__dismiss.artdeco-button.artdeco-button--circle.artdeco-button--muted.artdeco-button--1.artdeco-button--tertiary.ember-view")));
		                    closeBox.click();
		        		} catch (Exception e) {
		        		    System.out.println("original Link copy fail : " + e.getMessage());
		        		}
		        	} catch(NoSuchElementException e) {
		        		System.out.println("can't find element <<<<<<<<<<<<<<<<<<<<: "+e);
		        	}
		        	WebElement name = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu > div > div > a.app-aware-link.update-components-actor__meta-link > span.update-components-actor__title > span.update-components-actor__name.hoverable-link-text.t-14.t-bold.t-black > span > span:nth-child(1)"));
		            String authors = author.getText();
		            String nameText = name.getText();
		            System.out.println("author is :"+authors);
		            System.out.println("content is : "+content);
		            System.out.println("name is : "+ nameText);
		            System.out.println("url is : "+ originalPage);
//		            articleDAO.saveLinkedinArticle(authors, content, nameText, originalPage);
		        }
		    }
		} catch (Exception e) {
		    System.err.println("An error occurred: " + e.getMessage());
		    e.printStackTrace();
		}
//		driver.quit();
	}
	

    
    // 컨텐츠 내부에 링크 박스를 알아내기
    private static String getLinkBoxSafe(WebElement div) {
        try {
        	WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
            return contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
        } catch (NoSuchElementException e) {
            System.out.println(div + " not found: " + e);
            return "";
        }
    }
    

    
}
