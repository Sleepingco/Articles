package com.toyproject.scraping;

import org.jsoup.nodes.Element;

public class ContentFilter_jojoldu {
    static StringBuilder extractContent(Element element, StringBuilder builder) {
        if (!element.ownText().isEmpty()) {
            builder.append(element.ownText()).append("\n");
        }
    	// 순회하며 텍스트와 이미지 추출
        for (Element child : element.children()) {
            if (child.tagName().equals("img")) {
                // 이미지의 src URL 출력
                String imageUrl = child.absUrl("src");
                builder.append(imageUrl).append("\n");
            } else {
                extractContent(child, builder); // 재귀적으로 내부 요소도 처리
            }
        }
		return builder;
    }
}
