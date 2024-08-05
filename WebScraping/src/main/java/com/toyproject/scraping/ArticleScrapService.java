package com.toyproject.scraping;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
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
		log.info("start");
		LocalDateTime now = LocalDateTime.now();
		//length : 배열의 길이 알려 할 때
		//length() : 문자열의 길이를 알려 할 때
		//size() : Collection, 자료구조의 크기를 알려 할 때
		UrlManager myurl = new UrlManager();
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
            
			//헤더 없음 페이지 이동
			Document jojolduDocument = Jsoup.connect(urljojoldu).get();
			
			// 최신글 url 인덱스 및 최신글 url
            String jojolduHref = jojolduDocument.selectFirst("#content > div.cover-thumbnail-2 > ul > li:nth-child(1) > a").attr("href");
            String jojolduHrefNum = jojolduHref.replaceAll("/","");
            
            // 반복문 끝을 위한 인덱스
            int jojolduHrefIdx = Integer.parseInt(jojolduHrefNum);
            
            // 최신 url
            String jojolduNewHref = urljojoldu+jojolduHrefNum;
            
            // DB내에 가장 최신글url db에 데이터가 없을때 기본 url로 지정
            int testId  = 1;
			String testSite = "티스토리";			
            ArticleDTO jojolduTopUrlDTO = articleDAO.getNewestUrl(testId,testSite);
            if(jojolduTopUrlDTO == null) {
            	jojolduTopUrlDTO = new ArticleDTO();
            	jojolduTopUrlDTO.setOriginalpage(urljojoldu);
            }
            
            // DB에 가장 최신 URL에 인덱스를 추출하기위한 부분
            String jojolduTopUrl = jojolduTopUrlDTO.getOriginalpage();            
            if (jojolduTopUrl != null && !jojolduTopUrl.isEmpty()) {
                String jojolduTopUrlIdx = jojolduTopUrl.substring(jojolduTopUrl.lastIndexOf("/") + 1);
                if(jojolduTopUrlIdx ==null||jojolduTopUrlIdx.equals("")) {
                	jojolduTopUrlIdx = "0";
                }
                try {
                    int jojolduLatestUrlIdxInt = Integer.parseInt(jojolduTopUrlIdx);
                    
        			for(int idx = 1; idx<=jojolduHrefIdx; idx++) {
        				//https://jojoldu.tistory.com/category?page=1의 첫번째 요소의 링크만큼
        				Thread.sleep(2000);
        				String jojolduCurUrl = urljojoldu + idx;
        				try {
        					
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
        			                    
        			            // 작성일자
        						Element creationdateDiv = jojolduDocuments.selectFirst("#content > div > div.post-cover > div > span.meta > span.date");
        						// text 추출
        		            	String title = titleDiv.text();
        		            	String finContent = processedContent.toString();
        		                String creationdate = creationdateDiv.text();
        		                String blogName = "티스토리";
        		                int id = 1;
        		                if(idx<=jojolduLatestUrlIdxInt) {
        		                	System.out.println("update exist article : " + idx);
        		                	articleDAO.updateArticle(title,finContent,creationdate,jojolduCurUrl,id);
        		                } else {
        		                	System.out.println("insert new article : " + idx);
        		                	articleDAO.saveArticle(title, finContent, jojolduCurUrl, creationdate, blogName, id);
        		                }
        		                printMemoryUsage();
        					} else {
        						System.out.println("Element not found at index: " + idx);
        					}
        				}catch (IOException e) {
        			        System.out.println("Failed to retrieve data from index " + idx + ": " + e.getMessage());       			        
        				} catch (Exception e) {
        					System.out.println("ErrorMessage for scrap : "+e);	
        				}
        			}
            		
                } catch (NumberFormatException e) {
                    System.out.println("URL의 마지막 부분이 숫자가 아닙니다: " + jojolduTopUrlIdx);
                }
            } else {
                System.out.println("URL이 null이거나 비어 있습니다.");
            }

			LocalDateTime later = LocalDateTime.now();
			long millisDifference = Duration.between(now, later).toMillis();
			log.info("end");
			System.out.println("Tistory code took "+millisDifference+"ms");		
		} catch (Exception e ) {
			System.out.println("ErrorMessage for Connect : "+e);
		}
	}
	private void printMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed() / 1024 / 1024;
        System.out.println("Heap memory used: " + usedMemory + " MB");
    }
	
}
