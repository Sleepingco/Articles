package com.toyproject.scraping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Controller
public class MainController {
	@GetMapping("/")
	public String home() {
		urlclass myurl = new urlclass();
		String url = myurl.getUrl();
		String[] productInfo = new String[] {
				"모델번호 : ",
				"출시일 : ",
				"컬러 : ",
				"발매가 : "
		};
		//length : 배열의 길이 알려 할 때
		//length() : 문자열의 길이를 알려 할 때
		//size() : Collection, 자료구조의 크기를 알려 할 때
		for(int productNum = 1000; productNum <=1003; productNum++) {
			try {
				Thread.sleep(3000);
				Connection conn = Jsoup.connect(url);
				Document document = conn.get();
				//상품 이미지 url
				Element imageUrl = document.getElementsByAttributeValue("alt", "상품 이미지").first();
				
				//브랜드
				Element brand = document.getElementsByClass("brand").first();
				
				//상품명
                Element title = document.getElementsByClass("sub_title").first();

                //상품정보
                Elements info = document.getElementsByClass("product_info");
				System.out.println("productNumber : "+productNum);
				System.out.println("상품 이미지  url : "+ imageUrl.attr("abs:src"));
				System.out.println("브랜드 : " + brand.text());
				System.out.println("상품명 : " + title.text());
				 for (int infoIdx = 0; infoIdx < info.size(); infoIdx++) {
	                    System.out.println(productInfo[infoIdx] + info.get(infoIdx).text());
	             }
				 System.out.println("--------------");
			} catch (Exception e ) {
				System.out.println("ErrorMessage : "+e);
			}
		}
		return "/ex";
	}
	
}