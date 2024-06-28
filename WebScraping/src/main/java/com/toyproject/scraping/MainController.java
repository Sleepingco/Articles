package com.toyproject.scraping;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	@Autowired
	private ArticleScrapService articleScrapService;
	@GetMapping("/")
	@ResponseBody
	public String home() {
		articleScrapService.jojolduCrawlAndSaveArticles();
		return "/ex";
	}
}