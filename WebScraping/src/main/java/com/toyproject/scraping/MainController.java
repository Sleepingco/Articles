package com.toyproject.scraping;


import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<JSONObject> home() {
		
//		//이동욱 티스토리
//		ArticleScrapService ASS = new ArticleScrapService(articleDAO);
//		ASS.jojolduCrawlAndSaveArticles();

//		//링크드인
//		SeleniumLinkedin SLI = new SeleniumLinkedin(articleDAO);
//		SLI.ScrapLinkedinSelenium();
//		
//		//커리어리
//		CareelySelenium CSI = new CareelySelenium(articleDAO);
//		CSI.ScrapCareelySelenium();
		
		ArrayList<ArticleDTO> alBoard=articleDAO.getArticleList();
		JSONArray ja = new JSONArray();
		for(int i=0; i<alBoard.size();i++) {
			JSONObject jo = new JSONObject();
			jo.put("title", alBoard.get(i).getTitle());
			jo.put("content", alBoard.get(i).getContent());
			jo.put("creationdate", alBoard.get(i).getCreationdate());
			jo.put("site", alBoard.get(i).getSite());
			jo.put("id", alBoard.get(i).getId());
			jo.put("name", alBoard.get(i).getName());
			ja.add(jo);
		}
		ApiResponse response = new ApiResponse(200, "ok", ja);
        return ResponseEntity.ok(response.toJSON());
	}
//	@GetMapping("/summary")
//	@ResponseBody
//	public JSONObject summary() {
//		articleDAO.
//		return null;
//	}
}