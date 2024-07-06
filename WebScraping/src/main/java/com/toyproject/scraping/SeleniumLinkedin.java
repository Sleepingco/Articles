package com.toyproject.scraping;

import java.time.Duration;
import java.time.LocalDateTime;
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
		int idx = 0;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
		try {
			// 내용이 담겨있는 div 하나
		    List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
		    if (divs.isEmpty()) {
		        System.out.println("No divs found.");
		    } else {
		    	// 각요소 추출 글쓴이, 내용 등등..
		        for (WebElement div : divs) {
		        	WebElement author = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu > div > div > a.app-aware-link.update-components-actor__meta-link > span.update-components-actor__title > span.update-components-actor__name.hoverable-link-text.t-14.t-bold.t-black > span > span:nth-child(1)"));
		        	WebElement contentText = null;
		        	String contentLinkText = null;
		        	String htmlLink = null;
//		        	WebElement link = null;
		        	try {
		        		
		        		contentText = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span"));
		        		
		        		// 컨텐츠 내부에 링크 박스를 알아내기
		        		WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
		        		contentLinkText = contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
		        		
		        		// 컨턴츠 내부에 링크 알아내기
		        		String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
		        		Document doc = Jsoup.parseBodyFragment(htmlContent);
		        		htmlLink = parseHtmlContent(doc);
		        		System.out.println("doc : "+doc);
//		        		System.out.println("hmtl text content : "+idx+" "+htmlLink);
//		        		System.out.println("hmtl text content link box : "+idx+" "+contentLinkText);
		        		idx++;
		        	} catch(NoSuchElementException e) {
		        		System.out.println("can't find element : "+e);
		        	}
		        	WebElement name = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu > div > div > a.app-aware-link.update-components-actor__meta-link > span.update-components-actor__title > span.update-components-actor__name.hoverable-link-text.t-14.t-bold.t-black > span > span:nth-child(1)"));

//		            String authors = author.getText();
//		            String contents = content.getText();
//		            String names = name.getText();
//		            articleDAO.saveLinkedinArticle(authors, contents, names, urljojoldu);
		        }
		    }
		} catch (Exception e) {
		    System.err.println("An error occurred: " + e.getMessage());
		    e.printStackTrace();
		}
		driver.quit();
	}
	
	// 작동안함 체크해야함
    public static String parseHtmlContent(Document doc) {
        StringBuilder textWithLinks = new StringBuilder();
        for (Element element : doc.body().children()) {
            if (element.tagName().equals("a")) {
                textWithLinks.append(element.text())
                             .append(" (")
                             .append(element.attr("href"))
                             .append(") ");
            } else {
                textWithLinks.append(element.text());
            }
        }
        return textWithLinks.toString();
    }
    
    
}
