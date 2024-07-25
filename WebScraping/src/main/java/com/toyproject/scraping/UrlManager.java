package com.toyproject.scraping;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class UrlManager {
    private Map<Integer, String> linkedinUrls;
    private Map<Integer, String> careelyUrls;
    private final String jojolduTistoryUrl = "https://jojoldu.tistory.com/";
    
    // 생성자에서 초기화
    public UrlManager() {
        linkedinUrls = new HashMap<>();
        careelyUrls = new HashMap<>();
        initializeUrlManagers();
    }
    
    private final void initializeUrlManagers() {
        careelyUrlManager();
        linkedinUrlManager();
    }
    
    public void careelyUrlManager() {
		careelyUrls = new HashMap<>();
		careelyUrls.put(2, "https://careerly.co.kr/profiles/669213");
//		careelyUrls.put(3, "https://careerly.co.kr/@totuworld");
//		careelyUrls.put(4, "https://careerly.co.kr/@hajoeun");
	}
	public void linkedinUrlManager() {
		linkedinUrls = new HashMap<>();
		linkedinUrls.put(1, "https://www.linkedin.com/in/%EB%8F%99%EC%9A%B1-%EC%9D%B4-575160177/recent-activity/all/");
		linkedinUrls.put(3, "https://www.linkedin.com/in/totuworld/recent-activity/all/");
	}
    
    // 모든 이름과 URL을 Map으로 반환하기
    public Map<Integer, String> getLinkedinUrls() {
        return new HashMap<>(linkedinUrls); // 변경 불가능한 복사본 반환
    }
    
    public Map<Integer, String> getCareelyUrls() {
        return new HashMap<>(careelyUrls); // 변경 불가능한 복사본 반환
    }
}
