package com.toyproject.scraping;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	@GetMapping("/testforscrap")
	@ResponseBody
	@Operation(summary = "아티클 전체 스크랩(누르지마세요)", description = "아직 최적화중 누를시 스크랩이 오래걸림")
    
	public void test() {
//		//이동욱 티스토리
//		ArticleScrapService ASS = new ArticleScrapService(articleDAO);
//		ASS.jojolduCrawlAndSaveArticles();

		//링크드인
		SeleniumLinkedin SLI = new SeleniumLinkedin(articleDAO);
		SLI.ScrapLinkedinSelenium();
		
		//커리어리
//		CareelySelenium CSI = new CareelySelenium(articleDAO);
//		CSI.ScrapCareelySelenium();
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/articles")
	@ResponseBody
    @Operation(summary = "아티클 전체 불러오기", description = "주어진 offset과 limit을 사용하여 아티클을 불러옵니다.",
    parameters = {
        @Parameter(name = "offset", description = "인덱스 시작 위치", required = true, example = "0"),
        @Parameter(name = "limit", description = "한 번에 불러올 아티클 수", required = true, example = "10"),
        @Parameter(name = "site", description = "사이트", required = false, example = "커리어리,티스토리,링크드인"),
        @Parameter(name = "title", description = "제목(제목이 없는 경우도 있음)", required = false, example = ""),
        @Parameter(name = "content", description = "내용", required = false, example = ""),
        @Parameter(name = "writer", description = "작성자", required = false, example = "")
    })
	public ResponseEntity<ApiResponse<JSONArray>> articles(HttpServletRequest req) {
		// 아티클 전체 불러오기
		try {
			String offsetParam = req.getParameter("offset");
			String limitParam = req.getParameter("limit");
			int offset = offsetParam != null ? Integer.parseInt(offsetParam) : 0;
	        int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;
	        String siteInput = req.getParameter("site");
	        
	        // site가 여러 개의 값으로 들어올 수 있으므로 이를 처리
	        String[] validSites = {"링크드인", "커리어리", "티스토리"};
	        List<String> siteList = new ArrayList<>();

	        // site 값이 여러 개의 콤마로 구분되어 있을 경우 처리
//	        if (siteInput != null && !siteInput.isEmpty()) {
//	            String[] sitesFromParam = siteInput.split(",");  // 콤마로 구분된 파라미터 처리
//	            for (String s : sitesFromParam) {
//	                if (Arrays.asList(validSites).contains(s.trim())) {
//	                    siteList.add(s.trim());  // MyBatis에서는 따옴표 없이 리스트로 전달
//	                }
//	            }
//	        }
	        
//	        contains()를 안썼을때 코드가 길어짐 하지만 재밌는 코드임 배열에서는 contains() 지원안함 list만 지원함
	        if (siteInput != null && !siteInput.isEmpty()) {
	        	String[] sitesFromParam = siteInput.split(",");
	        	for (String s : sitesFromParam) {
			        boolean found = false;
			        for (String site : validSites) {
			            if (site.equals(s.trim())) {
			                found = true;
			                break;
			            }
			        }
			        if (found) {
			            siteList.add(s.trim());
			        }
		        }
	        }
	        
	        String title = req.getParameter("title");
	        String content = req.getParameter("content");
	        String writer = req.getParameter("writer");

	        if (offset < 0 || limit < 0) {
	            return ResponseEntity.badRequest().body(new ApiResponse<>(500, "Internal Server Error", null));
	        }

	        
			ArrayList<ArticleDTO> alBoard=articleDAO.getArticleList(siteList, title, content, limit, offset, writer);;
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
				jo.put("thumbsurl", alBoard.get(i).getThumbsurl());
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
                jo.put("id", alDev.get(i).getId());
                jo.put("thumbsurl", alDev.get(i).getThumbsurl());
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