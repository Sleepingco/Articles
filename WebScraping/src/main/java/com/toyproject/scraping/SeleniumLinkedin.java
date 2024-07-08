package com.toyproject.scraping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
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
	public void JojolduSeleniumLinkedin() {
		UrlManager myurl = new UrlManager();
		
//		String urljojoldu = myurl.getJojolduLinkedinUrl();
//		String totuworldLinkedinUrl = myurl.getTotuworldLinkedinUrl();
//		List<String> urls = new ArrayList<>();
//		urls.add(urljojoldu);
//		urls.add(totuworldLinkedinUrl);
		
        
		// driver porperty and make new driver
		System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		WebDriver driver = new ChromeDriver(options);
		LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
		
		UrlManager manager = new UrlManager();
		Map<String, String> allUrls = manager.getAllUrls();
        login.loginToLinkedIn();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int cnt = 0;
        try {
        	for (Map.Entry<String, String> entry : allUrls.entrySet()) {
            	String urls = entry.getValue();
    			driver.get(urls);  // 무한 스크롤이 적용된 페이지 URL
    			
    			 // 처음에 페이지의 상태가 완전히 로드될 때까지 기다립니다.
                wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));

                AtomicLong lastHeight = new AtomicLong((long) js.executeScript("return document.body.scrollHeight"));

    			while (true) {
    				 
    			    // 페이지의 하단으로 스크롤
    			    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    			    
    			    // 페이지가 로드되고 새 콘텐츠가 나타날 때까지 기다립니다.
                    wait.until(webDriver -> js.executeScript("return document.body.scrollHeight > " + lastHeight));
    			    try {
    			        Thread.sleep(2000); 
    			    } catch (InterruptedException e) {
    			        Thread.currentThread().interrupt(); // 인터럽트 상태 복원
    			        System.err.println("스레드가 중단되었습니다: " + e.getMessage());
    			    }
    			    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
    			    if (newHeight == lastHeight.get()) {
    			        break; // 더 이상 로드되는 콘텐츠가 없으면 반복을 종료합니다.
    			    }
    			    lastHeight.set(newHeight);
    			}
    			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
//    			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//    			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
    			try {
    				// 내용이 담겨있는 div 하나
    			    List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
    			    Collections.reverse(divs);
    			    if (divs.isEmpty()) {
    			        System.out.println("No divs found.");
    			    } else {
    			    	// 각요소 추출 글쓴이, 내용 등등..
    			        for (WebElement div : divs) {
    			        	wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span")));
    			        	String content  = null;
    			        	String originalPage = null;
    			        	try {
    			        		// 컨텐츠 와 내부에 링크 알아내기
    			        		System.out.println("컨텐츠 와 내부에 링크 알아내기");
    			        		String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
    			        		Document doc = Jsoup.parseBodyFragment(htmlContent);
    			        		content = doc.text();
    			        		
    			        		// 컨텐츠 내부에 링크 박스를 알아내기
    			        		content += " "+getLinkBoxSafe(div);

    			        		// 원본 url 복사
    			        		originalPage = clikToCopyLink(div, wait, driver);
    			        	} catch(NoSuchElementException e) {
    			        		System.out.println("can't find element <<<<<<<<<<<<<<<<<<<<: "+e);
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
    			        }
    			    }
    			} catch (Exception e) {
    			    System.err.println("An error occurred: " + e.getMessage());
    			    e.printStackTrace();
    			}
            }
        }finally {
        	System.out.println("count is :" +cnt);
            driver.quit();
        }
        

	}
		
	

    
    // 컨텐츠 내부에 링크 박스를 알아내기
    private static String getLinkBoxSafe(WebElement div) {
        try {
        	WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
        	System.out.println("컨텐츠 내부에 링크 박스를 알아내기 성공");
        	return contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
            
        } catch (NoSuchElementException e) {
            System.out.println(div + " not found: " + e);
            return "";
        }
    }

    public void scrollToElementCentered(WebDriver driver, WebElement div) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "const viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
            "const elementTop = arguments[0].getBoundingClientRect().top;" +
            "window.scrollBy(0, elementTop-(viewPortHeight/2));", 
            div);
    }
    
    public String clikToCopyLink(WebElement div,  WebDriverWait wait, WebDriver driver ) {
    	String originalPage = null;
    	try {
    		scrollToElementCentered(driver, div);
    		WebElement button = div.findElement(By.cssSelector(".feed-shared-control-menu__trigger.artdeco-button.artdeco-button--tertiary.artdeco-button--muted.artdeco-button--1.artdeco-button--circle.artdeco-dropdown__trigger.artdeco-dropdown__trigger--placement-bottom.ember-view"));
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    	    WebElement linkShare = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".feed-shared-control-menu__item.option-share-via")));
    	    linkShare.click();
    	    WebElement shareBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.artdeco-toast-item__cta")));
    	    originalPage = shareBox.getAttribute("href");
//            Thread.sleep(1000);
            WebElement closeBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".artdeco-toast-item__dismiss.artdeco-button.artdeco-button--circle.artdeco-button--muted.artdeco-button--1.artdeco-button--tertiary.ember-view")));
            closeBox.click();
            
    	} catch (Exception e) {
    		System.out.println("----------------------------------------------------------------");
    	    System.out.println("original Link copy fail : " + e.getMessage());
    	}
    	return originalPage;
    }

    
}
