package com.toyproject.scraping;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArticleScrapService {
	@Autowired
	private ArticleDAO articleDAO;
	public ArticleScrapService(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	public void jojolduCrawlAndSaveArticles() {
		LocalDateTime now = LocalDateTime.now();
		//length : 배열의 길이 알려 할 때
		//length() : 문자열의 길이를 알려 할 때
		//size() : Collection, 자료구조의 크기를 알려 할 때
		urlclass myurl = new urlclass();
		String urljojoldu = myurl.getJojolduTistoryUrl();
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

            
			// 최신글 url 인덱스 및 최신글 url
            String jojolduHref = jojolduDocument.selectFirst("#content > div.cover-thumbnail-2 > ul > li:nth-child(1) > a").attr("href");
            String jojolduHrefNum = jojolduHref.replaceAll("/","");
            int jojolduHrefIdx = Integer.parseInt(jojolduHrefNum);
            String jojolduNewHref = urljojoldu+jojolduHrefNum;
            // DB내에 가장 최신글url
            ArticleDTO jojolduTopUrlDTO = articleDAO.jojolduLatestUrl();
            if(jojolduTopUrlDTO == null) {
            	jojolduTopUrlDTO = new ArticleDTO();
            	jojolduTopUrlDTO.setOriginalpage(urljojoldu);
            }
            String jojolduTopUrl = jojolduTopUrlDTO.getOriginalpage();
            String jojolduTopUrlIdx = jojolduTopUrl;
            jojolduTopUrlIdx = jojolduTopUrl.substring(jojolduTopUrl.lastIndexOf("/") + 1);
            int jojolduLatestUrlIdxInt = 0;
            if (jojolduTopUrlIdx != null && !jojolduTopUrlIdx.isEmpty()) {
                jojolduLatestUrlIdxInt = Integer.parseInt(jojolduTopUrlIdx);
            } else {
                System.out.println("URL이 null이거나 비어 있습니다.");
            }

    		if(!jojolduTopUrl.equals(jojolduNewHref)) {
    			for(int idx = jojolduLatestUrlIdxInt+1; idx<=jojolduHrefIdx; idx++) {
    				//https://jojoldu.tistory.com/category?page=1의 첫번째 요소의 링크만큼
    				Thread.sleep(3000);
    				String jojolduCurUrl = urljojoldu + idx;
    				try {
    					log.info("start");
    					Document jojolduDocuments = Jsoup.connect(jojolduCurUrl).get();
    					if (jojolduDocuments != null) {
    						// 제목
    						Element titleDiv = jojolduDocuments.selectFirst("#content > div > div.post-cover > div > h1");
    						// 본문 내용 class에 첫번째 요소를 가져옴
    						Element contentDiv = jojolduDocuments.selectFirst("#content > div > div.entry-content > div.contents_style");
    						StringBuilder processedContent = new StringBuilder();
    						if (contentDiv != null) {
    							StringBuilder contentBuilder = new StringBuilder();
    							processedContent = ContentFilter_jojoldu.extractContent(contentDiv, contentBuilder);
    			            } else {
    			                System.out.println("Content div not found");
    			            }
    						
    			            // 작성자
    						Element authorDiv = jojolduDocuments.selectFirst(".author");           
    			            // 작성일자
    						Element creationdateDiv = jojolduDocuments.selectFirst("#content > div > div.post-cover > div > span.meta > span.date");
    						

    						// text 추출
    		            	String title = titleDiv.text();
    		            	String finContent = processedContent.toString();
    		                String author = authorDiv.text();
    		                String creationdate = creationdateDiv.text();
    		                String blogName = "티스토리";
    		                articleDAO.savearticle(title, finContent, author, jojolduCurUrl, creationdate, blogName);
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
    		}

			LocalDateTime later = LocalDateTime.now();
			long millisDifference = Duration.between(now, later).toMillis();
			log.info("end");
			System.out.println("code took "+millisDifference+"ms");

            
//			
//			// 제목 
//			Elements titleDiv = document.getElementsByTag("h1");
//			// 본문 내용 class에 첫번째 요소를 가져옴
//            Element contentDiv = document.selectFirst(".tt_article_useless_p_margin.contents_style");
//            // 작성자
//            Element authorDiv= document.selectFirst("#sidebar > div > div:nth-child(1) > div > div.text-h-400.text-sm");           
//            // 작성일자
//            Elements creationdateDiv = document.getElementsByTag("time");

		} catch (Exception e ) {
			System.out.println("ErrorMessage for Connect : "+e);
		}
	}
	
}
