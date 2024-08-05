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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Parameter;


@Controller
public class MainController {
	//http://localhost:8080/swagger-ui/index.html#
	
	@Autowired
	private ArticleDAO articleDAO;
	@SuppressWarnings("unchecked")
	@GetMapping("/")
	@ResponseBody
	public void test() {
		//이동욱 티스토리
//		ArticleScrapService ASS = new ArticleScrapService(articleDAO);
//		ASS.jojolduCrawlAndSaveArticles();

//		//링크드인
//		SeleniumLinkedin SLI = new SeleniumLinkedin(articleDAO);
//		SLI.ScrapLinkedinSelenium();
		
//		//커리어리
//		CareelySelenium CSI = new CareelySelenium(articleDAO);
//		CSI.ScrapCareelySelenium();
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/articles")
	@ResponseBody
    @Operation(summary = "아티클 전체 불러오기", description = "주어진 offset과 limit을 사용하여 아티클을 불러옵니다.",
    parameters = {
        @Parameter(name = "offset", description = "시작 위치", required = true, example = "0"),
        @Parameter(name = "limit", description = "한 번에 불러올 아티클 수", required = true, example = "10")
    })
	public ResponseEntity<ApiResponse<JSONArray>> articles(HttpServletRequest req) {
		// 아티클 전체 불러오기
		try {
			String offsetStr = req.getParameter("offset");
			String limitStr = req.getParameter("limit");
			int offset = offsetStr != null ? Integer.parseInt(offsetStr) : 0;
	        int limit = limitStr != null ? Integer.parseInt(limitStr) : 10;

	        if (offset < 0 || limit < 0) {
	            return ResponseEntity.badRequest().body(new ApiResponse<>(500, "Internal Server Error", null));
	        }

			
			ArrayList<ArticleDTO> alBoard=articleDAO.getArticleList(limit,offset);
			if (alBoard.isEmpty()) {
                ApiResponse<JSONArray> response = new ApiResponse<>(204, "No Content", null);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
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
		} catch (Exception e) {
            ApiResponse<JSONArray> response = new ApiResponse<>(500, "Internal Server Error", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
	}
	// 메인페이지 개발자 요약
	@SuppressWarnings("unchecked")
	@GetMapping("/summary")
	@ResponseBody
	public ResponseEntity<ApiResponse<JSONArray>> summary() {
        try {
            ArrayList<DevDTO> alDev = articleDAO.getDevSummary();
            
            if (alDev.isEmpty()) {
                ApiResponse<JSONArray> response = new ApiResponse<>(204, "No Content", null);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            JSONArray ja = new JSONArray();
            for (int i = 0; i < alDev.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("name", alDev.get(i).getName());
                jo.put("position", alDev.get(i).getPosition());
                jo.put("introduction", alDev.get(i).getIntroduction());
                ja.add(jo);
            }

            ApiResponse<JSONArray> response = new ApiResponse<>(200, "OK", ja);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<JSONArray> response = new ApiResponse<>(500, "Internal Server Error", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	// 개발자 상세 프로필
	@SuppressWarnings("unchecked")
	@GetMapping("/developers")
	@ResponseBody
	@Operation(summary = "개발자 프로필 불러오기", description = "id 값을 통해 특정 인물의 프로필을 가져옴",
    parameters = {
        @Parameter(name = "id", description = "개발자 id", required = true, example = "1"),
    })
	public ResponseEntity<ApiResponse<JSONArray>> developers(HttpServletRequest req) {
		try {
			String idStr = req.getParameter("id");
			int id = Integer.parseInt(idStr);
			ArrayList<DevDTO> alDev = articleDAO.getDevList(id);
			if (alDev.isEmpty()) {
                ApiResponse<JSONArray> response = new ApiResponse<>(204, "No Content", null);
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }
			JSONArray ja = new JSONArray();
			JSONParser parser = new JSONParser();
			try{
				for(int i =0; i<alDev.size();i++) {
					JSONObject jo = new JSONObject();
					jo.put("id",alDev.get(i).getId());
					jo.put("name",alDev.get(i).getName());
					jo.put("position",alDev.get(i).getPosition());
					jo.put("introduction",alDev.get(i).getIntroduction());
					jo.put("link",(JSONArray) parser.parse(alDev.get(i).getLink()));
					jo.put("career",(JSONArray) parser.parse(alDev.get(i).getCareer()));
					jo.put("thumbsurl",alDev.get(i).getThumbsurl());
					ja.add(jo);
				}
			}catch (ParseException e) {
	            // 예외 처리 (예: 로깅, 사용자에게 오류 메시지 반환 등)
				System.out.println(e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body(new ApiResponse<>(500, "Error parsing JSON", null));
	            
	        }
			ApiResponse<JSONArray> response = new ApiResponse<>(200, "OK", ja);
		    return ResponseEntity.ok(response);
		} catch (Exception e) {
			System.out.println(e);
            ApiResponse<JSONArray> response = new ApiResponse<>(500, "Internal Server Error", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
	}
}