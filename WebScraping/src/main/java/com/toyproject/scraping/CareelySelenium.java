package com.toyproject.scraping;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

public class CareelySelenium {
	@Autowired
	private ArticleDAO articleDAO;
	public CareelySelenium(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	public void ScrapCareelySelenium() {
		// 현재 시스템의 OS 이름을 가져옵니다.
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("Operating System: " + osName);

        // OS에 따라 ChromeDriver 경로를 설정합니다.
        if (osName.contains("win")) {
            // 윈도우즈의 경우
            System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
            System.out.println("Windows ChromeDriver is set.");
        } else if (osName.contains("mac")) {
            // 맥OS의 경우
        	System.setProperty("webdriver.chrome.driver", "/Users/parkseongho/git/WebScraping/WebScraping/chromedriver-mac-arm64/chromedriver");
            System.out.println("Mac ChromeDriver is set.");
        } else if (osName.contains("linux")) {
            // 리눅스의 경우
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            System.out.println("Linux ChromeDriver is set.");
        } else {
            System.out.println("Your OS is not supported!");
            // 지원되지 않는 운영체제일 경우, 추가적인 처리가 필요할 수 있습니다.
        }
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.get("https://careerly.co.kr/");
		
		// 로그인
		try {
			//윈
//			WebElement goLogin = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > nav > div > div.tw-flex.tw-relative.tw-items-center.tw-gap-1 > div:nth-child(2) > button"));
			// 맥
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
		}catch(Exception e) {
			System.out.println("failed login");
		}
		
		// url 불러오기
		UrlManager manager = new UrlManager();
		Map<Integer, String> allUrls = manager.getCareelyUrls();
		for (Map.Entry<Integer, String> entry : allUrls.entrySet()) {
			// url 이동
			String urls = entry.getValue();
			driver.get(urls);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("javascript:window.scrollTo(0,0)");
			
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			
			// 게시물 버튼 클릭
			WebElement postBtn = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > div.tw-bg-color-white.tw-relative.tw-min-h-screen > div:nth-child(3) > div > ul > li:nth-child(2) > button"));
			postBtn.click();
			// 페이지 맨아래 스크롤
			while (true) {			 
				// 현재 페이지의 높이를 저장
			    long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
			    // 페이지의 하단으로 스크롤
			    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			    try {
			        Thread.sleep(2000); 
			        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tw-bg-color-white")));
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
			
			
			
			try {
				int classCount = driver.findElements(By.cssSelector(".tw-flex.tw-justify-between.tw-items-center.tw-gap-3.tw-p-4")).size();
				System.out.println("class Count = " + classCount);
				classCount = classCount*2-1;
				
				
				for(int divCnt = classCount; divCnt>=1; divCnt-=2) {
					
					WebElement div = driver.findElement(By.cssSelector("#__next > div.ThemeProvider_theme-pc__QaFwS > div.css-1yctryj-SkeletonTheme > div > div.tw-border-solid.tw-border-color-slate-200.tw-border-0.tw-border-t > div > div > div > div > div > div:nth-child("+ divCnt +") > div"));
					// 더보기 누르기
					WebElement more = div.findElement(By.cssSelector("#__next > div.ThemeProvider_theme-pc__QaFwS > div.css-1yctryj-SkeletonTheme > div > div.tw-border-solid.tw-border-color-slate-200.tw-border-0.tw-border-t > div > div > div > div > div > div:nth-child("+divCnt+") > div > div:nth-child(2) > div > div > div > span > span"));
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", div);
					more.click();
					String site = "커리어리";
					int id = entry.getKey();
					String title = div.findElement(By.cssSelector(".tw-mb-6.tw-font-bold")).getText();
					String content = div.findElement(By.cssSelector(".ProseMirror.auto-line-break.tw-text-base.tw-text-color-slate-900.tw-whitespace-pre-wrap")).getText();
					String date = div.findElement(By.cssSelector(".tw-text-xs.tw-text-color-text-subtler")).getText();
					String originalPage = div.findElement(By.cssSelector(".tw-p-3.tw-flex.tw-flex-wrap.tw-justify-end.false")).getAttribute("href");
					
					
					Thread.sleep(500);
				    System.out.println("제목 : "+title);
				    System.out.println("컨텐츠 : "+content);
				    System.out.println("작성일 : "+date);
				    System.out.println("사이트 : "+originalPage);
				    
					ArticleDTO articleDTO = articleDAO.findArticleByIdentifier(originalPage,id);
					if (articleDTO != null) {
						// 기존 글 업데이트
						articleDAO.updateCareelyArticle(title,content,date,site,id);
					} else {
						// 새 글 삽입
						articleDAO.saveCareelyArticle(title,content,originalPage,date,site,id);
					}
					
				    
				}
				
			} catch(Exception e) {
				System.out.println("count failure : " + e);
			}
			driver.quit();
		}
		
	}
}
