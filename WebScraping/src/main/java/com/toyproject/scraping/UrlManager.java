package com.toyproject.scraping;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class UrlManager {
	private Map<String, String> linkedinUrls;
	private Map<String, String> careelyUrls;
	private String jojolduTistoryUrl = "https://jojoldu.tistory.com/";
	
	public void careelyUrlManager() {
		careelyUrls = new HashMap<>();
		careelyUrls.put("한기용", "https://careerly.co.kr/@totuworld?fa=search-result&fromArea=tab-all&queryId=32808631cb31e7c2d2de21cc948d5f4e&objectId=127914-64389");
		careelyUrls.put("송요창", "https://careerly.co.kr/@totuworld?fa=search-result&fromArea=tab-all&queryId=32808631cb31e7c2d2de21cc948d5f4e&objectId=127914-64389");
		careelyUrls.put("하조은", "https://careerly.co.kr/@hajoeun?fa=search-result&fromArea=tab-all&queryId=4d20f1358132e887c83258f1a29cf98e&objectId=16453-101915");
	}
	public void linkedinUrlManager() {
		linkedinUrls = new HashMap<>();
		linkedinUrls.put("이동욱", "https://www.linkedin.com/in/%EB%8F%99%EC%9A%B1-%EC%9D%B4-575160177/recent-activity/all/");
		linkedinUrls.put("송유창", "https://www.linkedin.com/in/totuworld/recent-activity/all/");
	}
	
	// 모든 이름과 URL을 Map으로 반환하기
    public Map<String, String> getLinkedinUrls() {
        return new HashMap<>(linkedinUrls); // 변경 불가능한 복사본 반환
    }
    
    public Map<String, String> getCareelyUrls() {
        return new HashMap<>(careelyUrls); // 변경 불가능한 복사본 반환
    }
}