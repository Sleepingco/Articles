package com.toyproject.scraping;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
public class MainController {
	@Autowired
	private articleDAO articleDAO;
	@GetMapping("/")
	@ResponseBody
	public String home() {
		LocalDateTime now = LocalDateTime.now();
		//length : 배열의 길이 알려 할 때
		//length() : 문자열의 길이를 알려 할 때
		//size() : Collection, 자료구조의 크기를 알려 할 때
		urlclass myurl = new urlclass();
		String urljojoldu = myurl.getJojolduTistoryUrl();
		StringBuilder contentBuilder = new StringBuilder();
		String newUrl = "newurl";
		if(urljojoldu.equals(newUrl)) {
			//기존 url과 새로운 url 비교
		}
		try {

			
//			//헤더 지정
//            Connection jojolduHome = Jsoup.connect(urljojoldu)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
//                    .header("Accept-Language", "en-US,en;q=0.9")
//                    .header("Accept-Encoding", "gzip, deflate, br")
//                    .header("Connection", "keep-alive")
//                    .header("Referer", "https://jojoldu.tistory.com/")
//                    .timeout(10 * 1000); // 타임아웃 설정 (10초)
//            
//          
//            Document jojolduDocument = jojolduHome.get();
            
			//헤더 없음
            Document jojolduDocument = Jsoup.connect(urljojoldu).get();
            
            //최신글 인덱스 알아내기
//            Element urlIndex = jojolduDocument.selectFirst("#content > div.cover-thumbnail-2 > ul > li:nth-child(1) > a");
//            String hrefValue = urlIndex.attr("href");
//            String hrefNum = hrefValue.replaceAll("/","");
//            int hrefIndex = Integer.parseInt(hrefNum);
//            System.out.println(hrefValue);

            
           
            String hrefValue = jojolduDocument.selectFirst("#content > div.cover-thumbnail-2 > ul > li:nth-child(1) > a").attr("href");
            String hrefNum = hrefValue.replaceAll("/","");
            int hrefIndex = Integer.parseInt(hrefNum);
            
            
			for(int idx = 767; idx<=767; idx++) {
				//https://jojoldu.tistory.com/category?page=1의 첫번째 요소의 링크만큼
				String curUrl = urljojoldu + idx;
				try {
					log.info("start");
					Document jojolduDocuments = Jsoup.connect(curUrl).get();
					if (jojolduDocuments != null) {
						// 제목
						Element titleDiv = jojolduDocuments.selectFirst("#content > div > div.post-cover > div > h1");
						// 본문 내용 class에 첫번째 요소를 가져옴
						Element contentDiv = jojolduDocuments.selectFirst("#content > div > div.entry-content > div.contents_style");
			            
						if (contentDiv != null) {
							StringBuilder finalContent = ContentFilter_jojoldu.extractContent(contentDiv, contentBuilder);
							System.out.println(finalContent);
			            } else {
			                System.out.println("Content div not found");
			            }
						
			            // 작성자
						Element authorDiv = jojolduDocuments.selectFirst(".author");           
			            // 작성일자
						Element creationdateDiv = jojolduDocuments.selectFirst("#content > div > div.post-cover > div > span.meta > span.date");
						
						
						
						
//						
//						System.out.println("titleDiv"+titleDiv);
//						System.out.println("contentDiv"+contentDiv);
//						System.out.println("authorDiv"+authorDiv);
//						System.out.println("creationdateDiv"+creationdateDiv);
						
						// text 추출
		            	String title = titleDiv.text();
		                String content =  contentDiv.text();
		                String author = authorDiv.text();
		                String creationdate = creationdateDiv.text();
		                
//						System.out.println("titleDiv"+title);
//						System.out.println("contentDiv"+content);
//						System.out.println("authorDiv"+author);
//						System.out.println("creationdateDiv"+creationdate);
		                articleDAO.savearticle(title, content, author, curUrl, creationdate);
					} else {
						System.out.println("Element not found at index: " + idx);
						continue;
					}
				}catch (IOException e) {
			        System.out.println("Failed to retrieve data from index " + idx + ": " + e.getMessage());
			        continue;
				} catch (Exception e ) {
					System.out.println("ErrorMessage for scrap : "+e);
					continue;
				} 
		        
			}
			LocalDateTime later = LocalDateTime.now();
			long secondsDifference = Duration.between(now, later).getSeconds();
			log.info("end");
//			System.out.println(secondsDifference);

            
//			
//			// 제목 
//			Elements titleDiv = document.getElementsByTag("h1");
//			// 본문 내용 class에 첫번째 요소를 가져옴
//            Element contentDiv = document.selectFirst(".tt_article_useless_p_margin.contents_style");
//            // 작성자
//            Element authorDiv= document.selectFirst("#sidebar > div > div:nth-child(1) > div > div.text-h-400.text-sm");           
//            // 작성일자
//            Elements creationdateDiv = document.getElementsByTag("time");
//            
//            
//            if (contentDiv != null) {
//            	String title = titleDiv.text();
//                String content =  contentDiv.text();
//                String author = authorDiv.text();
//                String creationdate = creationdateDiv.text();
//                
//                articleDAO.savearticle(title, content, author, url, creationdate);
//                
//                System.out.println("제목: " + titleDiv.text());
//                System.out.println("내용: " + contentDiv.text());
//                System.out.println("작성자: " + authorDiv.text());
//                System.out.println("작성일자: " + creationdateDiv.text());
//            } else {
//                System.out.println("해당 클래스를 가진 요소를 찾을 수 없습니다.");
//            }
		} catch (Exception e ) {
			System.out.println("ErrorMessage for Connect : "+e);
		}
		System.out.println("Completed processing");

		return "/ex";
	}
}