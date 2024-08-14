package com.toyproject.scraping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CareelySelenium {
    @Autowired
    private ArticleDAO articleDAO;

    public CareelySelenium(ArticleDAO articleDAO) {
        this.articleDAO = articleDAO;
    }

    public void ScrapCareelySelenium() {
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
        
        WebDriver driver = null;
        try {
        	ChromeOptions options = new ChromeOptions();
            options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            options.addArguments("headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(options);

            driver.get("https://careerly.co.kr/");

            try {
                WebElement goLogin = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > nav > div > div.tw-flex.tw-relative.tw-items-center.tw-gap-1 > div:nth-child(1) > button"));
                goLogin.click();
                Thread.sleep(2000);
                WebElement username = driver.findElement(By.id("email"));
                WebElement password = driver.findElement(By.id("password"));
                WebElement loginButton = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > div.tw-bg-slate-50.tw-flex.tw-flex-col.tw-w-full.tw-min-h-\\[calc\\(100vh-56px\\)\\] > div.tw-w-full.tw-grow.tw-mx-auto.tw-px-0.tw-bg-white.tw-max-w-3xl.tw-border-0.md\\:tw-border-l.md\\:tw-border-r.tw-border-solid.tw-border-slate-300 > div > form > div:nth-child(3) > button.tw-items-center.tw-justify-center.tw-border.tw-border-solid.tw-bg-slate-700.hover\\:tw-bg-slate-800.disabled\\:tw-bg-slate-100.tw-text-white.disabled\\:tw-text-slate-400.tw-border-slate-700.disabled\\:tw-border-slate-100.tw-text-base.tw-px-4.tw-py-3.tw-rounded.tw-font-bold.tw-block.tw-w-full.focus\\:tw-outline-none.focus-visible\\:tw-outline-none.focus-visible\\:tw-ring-2.focus-visible\\:tw-ring-color-slate-500.focus-visible\\:tw-ring-offset-2"));
                username.sendKeys("a8144320a@naver.com");
                password.sendKeys("Eofl123?");
                Thread.sleep(2000);
                loginButton.click();
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("failed login");
            }

            UrlManager manager = new UrlManager();
            Map<Integer, String> allUrls = manager.getCareelyUrls();
            for (Map.Entry<Integer, String> entry : allUrls.entrySet()) {
                String urls = entry.getValue();
                driver.get(urls);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("javascript:window.scrollTo(0,0)");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                WebElement postBtn = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > div.tw-bg-color-white.tw-relative.tw-min-h-screen > div:nth-child(3) > div > ul > li:nth-child(2) > button"));
                postBtn.click();
                while (true) {
                    long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    try {
                        Thread.sleep(2000);
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tw-bg-color-white")));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("스레드가 중단되었습니다: " + e.getMessage());
                    }
                    long newHeight = (long) js.executeScript("return document.body.scrollHeight");
                    if (newHeight == lastHeight) {
                        break;
                    }                             
                }
                try {
                    int classCount = driver.findElements(By.cssSelector(".tw-flex.tw-justify-between.tw-items-center.tw-gap-3.tw-p-4")).size();
                    classCount = classCount * 2 - 1;
                    for (int divCnt = classCount; divCnt >= 1; divCnt -= 2) {
                        WebElement div = driver.findElement(By.cssSelector("#__next > div.ThemeProvider_theme-pc__QaFwS > div.css-1yctryj-SkeletonTheme > div > div.tw-border-solid.tw-border-color-slate-200.tw-border-0.tw-border-t > div > div > div > div > div > div:nth-child(" + divCnt + ") > div"));
                        String site = "커리어리";
                        int id = entry.getKey();
                        WebElement more = null;
                        String content = "";
                        String title = null;
                        String date = null;
                        String originalPage = null;
                        try {
        					Thread.sleep(2000);
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				} // 이 부분은 네트워크 속도에 따라 조정 가능
                    	
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", div);
                        // 더보기 클릭
                        try {
                            more = div.findElement(By.cssSelector("#__next > div.ThemeProvider_theme-pc__QaFwS > div.css-1yctryj-SkeletonTheme > div > div.tw-border-solid.tw-border-color-slate-200.tw-border-0.tw-border-t > div > div > div > div > div > div:nth-child(" + divCnt + ") > div > div:nth-child(2) > div > div > div > span > span"));
                            more.click();
                        } catch (NoSuchElementException e) {
                            System.out.println("첫 번째 더보기 버튼을 찾을 수 없습니다. 퍼온 글일 수 있습니다.");
                            try {
                                more = div.findElement(By.cssSelector("#__next > div.ThemeProvider_theme-pc__QaFwS > div.css-1yctryj-SkeletonTheme > div > div.tw-border-solid.tw-border-color-slate-200.tw-border-0.tw-border-t > div > div > div > div > div > div:nth-child(" + divCnt + ") > div > div:nth-child(3) > div > div > div > span"));
                                more.click();
                                content = "(퍼옴) ";
                            } catch (NoSuchElementException e2) {
                                System.out.println("두 번째 더보기 버튼을 찾을 수 없습니다.");
                            }
                        }
                        // 제목 추출
                        try {
                            title = div.findElement(By.cssSelector(".tw-mb-6.tw-font-bold")).getText();
                        } catch (NoSuchElementException e) {
                            System.out.println("제목을 찾을 수 없습니다.");
                            title = "(제목 없음)";
                        }
                        try {
                            content += div.findElement(By.cssSelector(".ProseMirror.auto-line-break.tw-text-base.tw-text-color-slate-900.tw-whitespace-pre-wrap")).getText();
                            date = div.findElement(By.cssSelector(".tw-text-xs.tw-text-color-text-subtler")).getText();
                            originalPage = div.findElement(By.cssSelector(".tw-p-3.tw-flex.tw-flex-wrap.tw-justify-end.false")).getAttribute("href");
                        } catch (NoSuchElementException e) {
                            System.out.println("퍼온 글일 수 있습니다. 다른 선택자로 시도합니다.");
                        } finally {                       
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.err.println("Thread interrupted: " + e.getMessage());
                            }

                            System.out.println("제목 : " + title);
                            System.out.println("컨텐츠 : " + content);
                            System.out.println("작성일 : " + date);
                            System.out.println("사이트 : " + originalPage);

                            ArticleDTO articleDTO = articleDAO.findArticleByIdentifier(originalPage, id);
                            if (articleDTO != null) {
                                System.out.println("there is no new article");
                                articleDAO.updateArticle(title, content, date, originalPage, id);
                            } else {
                                System.out.println("there is a new article");
                                articleDAO.saveArticle(title, content, originalPage, date, site, id);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("scrap failure : " + e);
                }
                LocalDateTime later = LocalDateTime.now();
                long millisDifference = Duration.between(now, later).toMillis();
                log.info("end");
                System.out.println("Careely Scrap code took " + millisDifference + "ms");
            }
        }catch (Exception e) {
            log.error("An error occurred during the scraping process", e);
        } finally {
            if (driver != null) {
                driver.quit(); // WebDriver 종료
            }
        }
        

    }

}
