package com.toyproject.scraping;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class UrlManager {
	private Map<String, String> urls;
	private String jojolduTistoryUrl = "https://jojoldu.tistory.com/";
	
	public UrlManager() {
		urls = new HashMap<>();
	    urls.put("이동욱", "https://www.linkedin.com/in/%EB%8F%99%EC%9A%B1-%EC%9D%B4-575160177/recent-activity/all/");
	    urls.put("송유창", "https://www.linkedin.com/in/totuworld/recent-activity/all/");
	}
	
	// 모든 이름과 URL을 Map으로 반환하기
    public Map<String, String> getAllUrls() {
        return new HashMap<>(urls); // 변경 불가능한 복사본 반환
    }
}