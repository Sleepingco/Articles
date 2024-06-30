package com.toyproject.scraping;


import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MainController {
	@Autowired
	private ArticleDAO articleDAO;
	@SuppressWarnings("unchecked")
	@GetMapping("/")
	@ResponseBody
	public JSONArray home() {
		ArticleScrapService ASS = new ArticleScrapService(articleDAO);
		ASS.jojolduCrawlAndSaveArticles();
		ArrayList<ArticleDTO> alBoard=articleDAO.getArticleList();
		JSONArray ja = new JSONArray();
		for(int i=0; i<alBoard.size();i++) {
			JSONObject jo = new JSONObject();
			jo.put("title", alBoard.get(i).getTitle());
			jo.put("content", alBoard.get(i).getContent());
			jo.put("author", alBoard.get(i).getAuthor());
			jo.put("creationdate", alBoard.get(i).getCreationdate());
			ja.add(jo);
		}
		return ja;
	}
}