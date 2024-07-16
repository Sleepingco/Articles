package com.toyproject.scraping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
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
import java.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class SeleniumLinkedin {
	
	@Autowired
	private ArticleDAO articleDAO;
	public SeleniumLinkedin(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	public void ScrapLinkedinSelenium() {
		
		// driver porperty and make new driver
		System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		WebDriver driver = new ChromeDriver(options);
		LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
		
		UrlManager manager = new UrlManager();
		Map<String, String> allUrls = manager.getLinkedinUrls();
        login.loginToLinkedIn();
        int cnt = 0;
        for (Map.Entry<String, String> entry : allUrls.entrySet()) {
        	String urls = entry.getValue();
			driver.get(urls);  // 무한 스크롤이 적용된 페이지 URL
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			while (true) {
				 // 현재 페이지의 높이를 저장
			    long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
			    
			    // 페이지의 하단으로 스크롤
			    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			    try {
			        Thread.sleep(2000); 
			        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
			    } catch (InterruptedException e) {
			        Thread.currentThread().interrupt(); // 인터럽트 상태 복원
			        System.err.println("스레드가 중단되었습니다: " + e.getMessage());
			    }
			    // 스크롤 후 페이지 높이를 확인
			    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
			    if (newHeight == lastHeight) {
			        break; // 더 이상 로드되는 콘텐츠가 없으면 반복 종료
			    }
			}
//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
			try {
				// 내용이 담겨있는 div 하나
			    List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
			    // 요소의 순서를 뒤집음
			    Collections.reverse(divs);
			    if (divs.isEmpty()) {
			        System.out.println("No divs found.");
			    } else {
			    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")));
			    	// 각요소 추출 글쓴이, 내용 등등..
			        for (WebElement div : divs) {
			        	scrollToElementCentered(driver, div);
			        	String content  = null;
			        	String originalPage = null;
			        	try {
			        		// 원본 url 복사
			        		originalPage = clikToCopyLink(div, wait);
			        		// 컨텐츠 와 내부에 링크, 링크 박스 알아내기
			        		content = getContent(div)+" "+getLinkBoxSafe(driver, div);
			        	} catch(NoSuchElementException e) {
			        		System.out.println("can't find element: "+e);
			        	}
			        	
			        	WebElement name = driver.findElement(By.tagName("h3"));
			        	String nameText = entry.getKey();
			        	String authors = name.getText();
			            String siteName = "링크드인";
			            System.out.println("author is :"+authors);
			            System.out.println("content is : "+content);
			            System.out.println("name is : "+ nameText);
			            System.out.println("url is : "+ originalPage);
  
			            articleDAO.saveLinkedinArticle(authors, content, nameText, originalPage, siteName);
			            cnt++;
			            System.out.println("count is :" +cnt);
			        }
			    }
			} catch (Exception e) {
			    System.err.println("An error occurred: " + e.getMessage());
			    e.printStackTrace();
			}
			
        }
        System.out.println("total is :" +cnt);
        driver.quit();

	}
		
	private String getContent(WebElement div) {
		String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
		Document doc = Jsoup.parseBodyFragment(htmlContent);
		String content = doc.text();
		return content;
	}
	
	public void scrollToElementCentered(WebDriver driver, WebElement element) {
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript(
	        "const viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
	        "const elementTop = arguments[0].getBoundingClientRect().top;" +
	        "const offset = elementTop - viewPortHeight / 2;" +
	        "window.scroll({top: window.pageYOffset + offset, behavior: 'smooth'});",
	        element);
	}


    // 컨텐츠 내부에 링크 박스를 알아내기
    private String getLinkBoxSafe(WebDriver driver, WebElement div) {
        try {
        	WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
        	System.out.println("컨텐츠 내부에 링크 박스를 알아내기 성공");
        	return contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
            
        } catch (NoSuchElementException e) {
//            System.out.println(div + " not found: " + e);
            System.out.println("링크박스 없음 no link box");
            return null;
        }
    }

    
    
    public String clikToCopyLink(WebElement div,  WebDriverWait wait) {
    	String originalPage = null;
    	try {
    		WebElement button = div.findElement(By.cssSelector(".feed-shared-control-menu__trigger.artdeco-button.artdeco-button--tertiary.artdeco-button--muted.artdeco-button--1.artdeco-button--circle.artdeco-dropdown__trigger.artdeco-dropdown__trigger--placement-bottom.ember-view"));
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    	    WebElement linkShare = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".feed-shared-control-menu__item.option-share-via")));
    	    linkShare.click();
    	    WebElement shareBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.artdeco-toast-item__cta")));
    	    originalPage = shareBox.getAttribute("href");
            WebElement closeBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".artdeco-toast-item__dismiss.artdeco-button.artdeco-button--circle.artdeco-button--muted.artdeco-button--1.artdeco-button--tertiary.ember-view")));
            closeBox.click();
            
    	} catch (Exception e) {
    	    System.out.println("original Link copy fail : " + e.getMessage());
    	}
    	return originalPage;
    }

    
}
