package com.toyproject.scraping;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

public class CareelySelenium {
	@Autowired
	private ArticleDAO articleDAO;
	public CareelySelenium(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	public void ScrapCareelySelenium() {
		System.setProperty("webdriver.chrome.driver", "F:\\SHP\\PersonalProject\\WebScraping\\WebScraping\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.get("https://careerly.co.kr/");
		
		// 로그인
		try {
			WebElement goLogin = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > nav > div > div.tw-flex.tw-relative.tw-items-center.tw-gap-1 > div:nth-child(2) > button"));
			goLogin.click();
			Thread.sleep(2000);
	        WebElement username = driver.findElement(By.id("email"));
	        WebElement password = driver.findElement(By.id("password"));
	        WebElement loginButton = driver.findElement(By.cssSelector("#__next > div > div.css-1yctryj-SkeletonTheme > div.tw-bg-slate-50.tw-flex.tw-flex-col.tw-w-full.tw-min-h-\\[calc\\(100vh-56px\\)\\] > div.tw-w-full.tw-grow.tw-mx-auto.tw-px-0.tw-bg-white.tw-max-w-3xl.tw-border-0.md\\:tw-border-l.md\\:tw-border-r.tw-border-solid.tw-border-slate-300 > div > form > div:nth-child(3) > button.tw-items-center.tw-justify-center.tw-border.tw-border-solid.tw-bg-slate-700.hover\\:tw-bg-slate-800.disabled\\:tw-bg-slate-100.tw-text-white.disabled\\:tw-text-slate-400.tw-border-slate-700.disabled\\:tw-border-slate-100.tw-text-base.tw-px-4.tw-py-3.tw-rounded.tw-font-bold.tw-block.tw-w-full.focus\\:tw-outline-none.focus-visible\\:tw-outline-none.focus-visible\\:tw-ring-2.focus-visible\\:tw-ring-color-slate-500.focus-visible\\:tw-ring-offset-2"));
	        username.sendKeys("a8144320a@naver.com");
	        password.sendKeys("Eofl123?");
	        Thread.sleep(2000);
	        loginButton.click();
		}catch(Exception e) {
			System.out.println("failed login");
		}
		// 페이지 이동
		UrlManager manager = new UrlManager();
		Map<String, String> allUrls = manager.getCareelyUrls();
		for (Map.Entry<String, String> entry : allUrls.entrySet()) {
			
		}
	}
}
