package com.toyproject.scraping;


import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MainController {
	//http://localhost:8080/swagger-ui/index.html#
	
	@Autowired
	private ArticleDAO articleDAO;
//	@SuppressWarnings("unchecked")
//	@GetMapping("/articles")
//	@ResponseBody
//	public void test() {
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
//	}
	@SuppressWarnings("unchecked")
	@GetMapping("/articles")
	@ResponseBody
	public ResponseEntity<ApiResponse<JSONArray>> articles() {
		// 아티클 전체 불러오기
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
		
		ApiResponse<JSONArray> response = new ApiResponse<>(200, "OK", ja);
	    return ResponseEntity.ok(response);
	}
	// 메인페이지 개발자 요약
	@SuppressWarnings("unchecked")
	@GetMapping("/summary")
	@ResponseBody
	public ResponseEntity<ApiResponse<JSONArray>> summary() {
		ArrayList<DevDTO> alDev = articleDAO.getDevSummary();
		JSONArray ja = new JSONArray();
		for(int i =0; i<alDev.size();i++) {
			JSONObject jo = new JSONObject();
			jo.put("name",alDev.get(i).getName());
			jo.put("position",alDev.get(i).getPosition());
			jo.put("id",alDev.get(i).getIntroduction());
			ja.add(jo);
		}
		ApiResponse<JSONArray> response = new ApiResponse<>(200, "OK", ja);
	    return ResponseEntity.ok(response);
	}
	// 개발자 상세 프로필
	@SuppressWarnings("unchecked")
	@GetMapping("/developers")
	@ResponseBody
	public ResponseEntity<ApiResponse<JSONArray>> developers() {
		ArrayList<DevDTO> alDev = articleDAO.getDevList();
		JSONArray ja = new JSONArray();
		JSONParser parser = new JSONParser();
		try{
			for(int i =0; i<alDev.size();i++) {
				JSONObject jo = new JSONObject();
				jo.put("id",alDev.get(i).getId());
				jo.put("name",alDev.get(i).getName());
				jo.put("position",alDev.get(i).getPosition());
				jo.put("introduction",alDev.get(i).getIntroduction());
				jo.put("link",(JSONObject) parser.parse(alDev.get(i).getLink()));
				jo.put("career",(JSONObject) parser.parse(alDev.get(i).getCareer()));
				jo.put("thumbsurl",alDev.get(i).getThumbsurl());
				ja.add(jo);
			}
		}catch (ParseException e) {
            // 예외 처리 (예: 로깅, 사용자에게 오류 메시지 반환 등)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse<>(500, "Error parsing JSON", null));
        }
		
		ApiResponse<JSONArray> response = new ApiResponse<>(200, "OK", ja);
	    return ResponseEntity.ok(response);
	}
}