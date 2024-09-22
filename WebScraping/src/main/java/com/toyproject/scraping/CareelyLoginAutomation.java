package com.toyproject.scraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CareelyLoginAutomation {
	private WebDriver driver;
	
	public CareelyLoginAutomation(WebDriver driver) {
		this.driver = driver;
	}
	public void loginAttempt1() {
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
	}
}
