package com.toyproject.scraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        LocalDateTime now = LocalDateTime.now();
        log.info("Start scraping...");

        // Set ChromeDriver path based on OS
        String osName = System.getProperty("os.name").toLowerCase();
        log.info("Operating System: " + osName);

        if (osName.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
        } else if (osName.contains("mac")) {
            System.setProperty("webdriver.chrome.driver", "/Users/parkseongho/git/WebScraping/WebScraping/chromedriver-mac-arm64/chromedriver");
        } else if (osName.contains("linux")) {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        } else {
            log.error("Unsupported OS!");
            return;
        }
        // scraping start
        WebDriver driver = null;
        try {
        	// Setup ChromeOptions
            ChromeOptions options = new ChromeOptions();
            options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            options.addArguments("headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1280,720");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-notifications");
            
            options.addArguments("--disable-gpu");

            // Initialize WebDriver
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(90)); // Reduced timeout for faster operations

            // Hide WebDriver
            ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
            // Modify UserAgent via JavaScript
            ((JavascriptExecutor) driver).executeScript("navigator.__defineGetter__('userAgent', function(){ return 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36'; })");

            // Login to LinkedIn
            try {
                LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
                login.loginToLinkedIn();
            } catch (Exception e) {
                log.error("Login failed. Maybe already logged in.", e);
            }

            // Scraping process
            UrlManager manager = new UrlManager();
            Map<Integer, String> allUrls = manager.getLinkedinUrls();

            for (Map.Entry<Integer, String> entry : allUrls.entrySet()) {
                String url = entry.getValue();
                driver.get(url);

//                JavascriptExecutor js = (JavascriptExecutor) driver;
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container")));
                // 무한 스크롤 처리
//                long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
//
//                while (true) {
//                    // 페이지 끝까지 스크롤
//                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
//
//                    // 새로운 콘텐츠 로드를 기다림
//                    try {
//    					Thread.sleep(3000);
//    				} catch (InterruptedException e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				} // 이 부분은 네트워크 속도에 따라 조정 가능
//
//                    // 새로운 스크롤 높이 계산
//                    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
//
//                    // 새로운 콘텐츠가 로드되지 않았을 경우, 루프 종료
//                    if (newHeight == lastHeight) {
//                        break;
//                    }
//                    lastHeight = newHeight;
//                    log.info("down down");
//                }

                try {
                    List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
                    Collections.reverse(divs);

                    if (divs.isEmpty()) {
                        log.warn("No divs found.");
                    } else {
                        for (WebElement div : divs) {
                            scrollToElementCentered(driver, div);
                            try {
                            	
                            	try {
                					Thread.sleep(2000);
                				} catch (InterruptedException e) {
                					// TODO Auto-generated catch block
                					e.printStackTrace();
                				} // 이 부분은 네트워크 속도에 따라 조정 가능
                            	
                            	String originalPage = clickToCopyLink(driver, div, wait);
                                String content = getContent(div,wait) + " " + getLinkBoxSafe(driver, div);
                                String date = "";
                                try {
                                    date = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu > div > div > a.app-aware-link.update-components-actor__sub-description-link > span > span.visually-hidden")).getText();
                                    date = formatRelativeDateTime(date);
                                    System.out.println("date:"+date);
                                } catch (Exception e) {
                                	log.warn("퍼온 글일 수 있습니다. 다른 선택자로 시도합니다."+e);
                                    try {
                                        date = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex > div > div > a.app-aware-link.update-components-actor__sub-description-link > span > span.visually-hidden")).getText();
                                        date = formatRelativeDateTime(date);
                                        System.out.println("date:"+date);
                                        content = "(퍼옴) " + content;
                                    } catch (Exception e2) {
                                    	log.warn("두 번째 시도에서도 날짜를 찾을 수 없습니다."+e2);
                                    }
                                }

                                int id = entry.getKey();
                                String siteName = "링크드인";
                                log.info("Processing ID: {}", id);

                                ArticleDTO articleDTO = articleDAO.findArticleByIdentifier(originalPage, id);
                                if (articleDTO != null) {
                                    log.info("Updating existing article...");
                                    articleDAO.updateLinkedinArticle(content, date, originalPage, id);
                                } else {
                                    log.info("Inserting new article...");
                                    articleDAO.saveLinkedinArticle(content, originalPage, date, siteName, id);
                                }
                            } catch (NoSuchElementException e) {
                                log.error("Element not found, skipping to next.", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("An error occurred during scraping", e);
                }
            }

            LocalDateTime later = LocalDateTime.now();
            long millisDifference = Duration.between(now, later).toMillis();
            log.info("End of scraping process");
            log.info("Selenium Scrap code took {} ms", millisDifference);
        } catch (Exception e) {
            log.error("An error occurred during the scraping process", e);
        } finally {
            if (driver != null) {
                driver.quit(); // WebDriver 종료
            }
        }
        
    }

    private String getContent(WebElement div, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")));
            String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
            Document doc = Jsoup.parseBodyFragment(htmlContent);
            return doc.text();
        } catch (NoSuchElementException e) {
            log.warn("No content found.");
            return "";
        }
    }

    public void scrollToElementCentered(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "const viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
            "const elementTop = arguments[0].getBoundingClientRect().top;" +
            "const offset = elementTop - viewPortHeight / 2;" +
            "window.scroll({top: window.pageYOffset + offset});", element);
    }

    private String getLinkBoxSafe(WebDriver driver, WebElement div) {
        try {
            WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
            log.debug("Successfully found link box inside content.");
            return contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
        } catch (NoSuchElementException e) {
            log.warn("No link box found.");
            return "";
        }
    }

    public String clickToCopyLink(WebDriver driver, WebElement div, WebDriverWait wait) {
        String originalPage = null;
        try {
            WebElement button = div.findElement(By.cssSelector(".feed-shared-control-menu__trigger.artdeco-button.artdeco-button--tertiary.artdeco-button--muted.artdeco-button--1.artdeco-button--circle.artdeco-dropdown__trigger.artdeco-dropdown__trigger--placement-bottom.ember-view"));
            scrollToElementCentered(driver, button); // Ensure the button is visible
            wait.until(ExpectedConditions.visibilityOf(button));
            wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            
            WebElement linkShare = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".feed-shared-control-menu__item.option-share-via")));
            linkShare.click();
            
            WebElement shareBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.artdeco-toast-item__cta")));
            originalPage = shareBox.getAttribute("href");
            
            WebElement closeBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".artdeco-toast-item__dismiss.artdeco-button.artdeco-button--circle.artdeco-button--muted.artdeco-button--1.artdeco-button--tertiary.ember-view")));
            closeBox.click();
        } catch (Exception e) {
            log.error("Failed to copy original link", e);
        }
        return originalPage;
    }
    
    public static String formatRelativeDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.d"); // 날짜만 출력하는 포맷
        LocalDateTime now = LocalDateTime.now();

        try {
            if (input.contains("분 전")) {
                int minutes = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusMinutes(minutes);
                return resultDateTime.format(formatter);
            } else if (input.contains("시간 전")) {
                int hours = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusHours(hours);
                return resultDateTime.format(formatter);
            } else if (input.contains("일 전")) {
                int days = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusDays(days);
                return resultDateTime.format(formatter);
            } else if (input.contains("주 전")) {
                int weeks = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusWeeks(weeks);
                return resultDateTime.format(formatter);
            } else if (input.contains("개월 전")) {
                int months = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusMonths(months);
                return resultDateTime.format(formatter);
            } else if (input.contains("년 전")) {
                int years = Integer.parseInt(input.replaceAll("[^0-9]", ""));
                LocalDateTime resultDateTime = now.minusYears(years);
                return resultDateTime.format(formatter);
            } 
        } catch (NumberFormatException e) {
            // 숫자 변환 실패 시 로그를 남김
            System.err.println("날짜 변환 중 오류 발생: " + input);
            e.printStackTrace();
            return "Invalid date format";
        }

        return "Invalid date format"; // 입력이 인식되지 않은 경우
    }

}

