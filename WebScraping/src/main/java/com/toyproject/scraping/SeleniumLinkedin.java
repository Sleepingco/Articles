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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        log.info("start");

        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("Operating System: " + osName);

        if (osName.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
            System.out.println("Windows ChromeDriver is set.");
        } else if (osName.contains("mac")) {
            System.setProperty("webdriver.chrome.driver", "/Users/parkseongho/git/WebScraping/WebScraping/chromedriver-mac-arm64/chromedriver");
            System.out.println("Mac ChromeDriver is set.");
        } else if (osName.contains("linux")) {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            System.out.println("Linux ChromeDriver is set.");
        } else {
            System.out.println("Your OS is not supported!");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        options.addArguments("headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080"); // 윈도우 사이즈 설정

        WebDriver driver = new ChromeDriver(options);

        LinkedInLoginAutomation login = new LinkedInLoginAutomation(driver);
        login.loginToLinkedIn();

        UrlManager manager = new UrlManager();
        Map<Integer, String> allUrls = manager.getLinkedinUrls();

        int cnt = 0;
        for (Map.Entry<Integer, String> entry : allUrls.entrySet()) {
            String urls = entry.getValue();
            driver.get(urls);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 무한 스크롤 처리
            int maxScrollAttempts = 10;
            int scrollAttempts = 0;
            while (scrollAttempts < maxScrollAttempts) {
                long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                if (newHeight == lastHeight) {
                    scrollAttempts++;
                } else {
                    scrollAttempts = 0;
                }
                printMemoryUsage();
                
            }

            try {
                List<WebElement> divs = driver.findElements(By.cssSelector("#fie-impression-container"));
                Collections.reverse(divs);
                if (divs.isEmpty()) {
                    System.out.println("No divs found.");
                } else {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")));
                    for (WebElement div : divs) {
                        scrollToElementCentered(driver, div);
                        try {
                            String originalPage = clickToCopyLink(driver, div, wait);
                            String content = getContent(div) + " " + getLinkBoxSafe(driver, div);
                            String date = "";
                            try {
                                date = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.update-components-actor--with-control-menu.align-items-flex-start > div > div > span > span.visually-hidden")).getText();
                            } catch (Exception e) {
                                System.out.println("퍼온 글일 수 있습니다. 다른 선택자로 시도합니다.");
                                try {
                                    date = div.findElement(By.cssSelector("#fie-impression-container > div.relative > div.update-components-actor.display-flex.align-items-flex-start > div > div > span > span:nth-child(1)")).getText();
                                    content = "(퍼옴) " + content;
                                } catch (Exception e2) {
                                    System.out.println("두 번째 시도에서도 날짜를 찾을 수 없습니다.");
                                }
                            }
                            int id = entry.getKey();
                            String siteName = "링크드인";
                            System.out.println("id : " + id);
                            System.out.println("content is : " + content);
                            System.out.println("date is : " + date);
                            System.out.println("url is : " + originalPage);
                            printMemoryUsage();
                            
                            ArticleDTO articleDTO = articleDAO.findArticleByIdentifier(originalPage, id);
                            if (articleDTO != null) {
                                System.out.println("update exist article");
                                articleDAO.updateLinkedinArticle(content, date, originalPage, id);
                            } else {
                                System.out.println("insert a new article");
                                articleDAO.saveLinkedinArticle(content, originalPage, date, siteName, id);
                            }
                        } catch (NoSuchElementException e) {
                            System.out.println("can't find element: " + e);
                        }
                        cnt++;
                        System.out.println("count is :" + cnt);
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("total is :" + cnt);
        driver.quit();

        LocalDateTime later = LocalDateTime.now();
        long millisDifference = Duration.between(now, later).toMillis();
        log.info("end");
        System.out.println("Selenium Scrap code took " + millisDifference + "ms");
    }

    private String getContent(WebElement div) {
        try {
            String htmlContent = div.findElement(By.cssSelector("#fie-impression-container > div.feed-shared-update-v2__description-wrapper.mr2 > div > div > span > span")).getAttribute("innerHTML");
            Document doc = Jsoup.parseBodyFragment(htmlContent);
            String content = doc.text();
            return content;
        } catch (NoSuchElementException e) {
            System.out.println("컨텐츠 없음 no content");
            return "";
        }
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

    private String getLinkBoxSafe(WebDriver driver, WebElement div) {
        try {
            WebElement contentLinkBox = div.findElement(By.cssSelector("#fie-impression-container > article > div"));
            System.out.println("컨텐츠 내부에 링크 박스를 알아내기 성공");
            return contentLinkBox.findElement(By.tagName("a")).getAttribute("href");
        } catch (NoSuchElementException e) {
            System.out.println("링크박스 없음 no link box");
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
            
            // 추가 대기 시간 설정
            Thread.sleep(1000);
            
            WebElement linkShare = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".feed-shared-control-menu__item.option-share-via")));
            linkShare.click();
            
            // 추가 대기 시간 설정
            Thread.sleep(1000);
            
            WebElement shareBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.artdeco-toast-item__cta")));
            originalPage = shareBox.getAttribute("href");
            
            WebElement closeBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".artdeco-toast-item__dismiss.artdeco-button.artdeco-button--circle.artdeco-button--muted.artdeco-button--1.artdeco-button--tertiary.ember-view")));
            closeBox.click();
        } catch (Exception e) {
            System.out.println("original Link copy fail : " + e.getMessage());
        }
        return originalPage;
    }

    private void printMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed() / 1024 / 1024;
        System.out.println("Heap memory used: " + usedMemory + " MB");
    }


}
