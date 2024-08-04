package com.toyproject.scraping;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.json.simple.JSONArray;

@Mapper
public interface ArticleDAO {
	ArrayList<ArticleDTO> getArticleList(int limit, int offset2);
	
	void saveArticle(String title, String content,String originalpage,String date, String site, int id);
	void updateArticle(String title, String content, String date, String originalPage, int id);
	void saveLinkedinArticle(String content, String originalpage,String date, String siteName,int id); 
	void updateLinkedinArticle(String content, String date, String originalPage, int id);
	
	ArticleDTO getNewestUrl(int testId,String site);
	ArticleDTO findArticleByIdentifier(String originalPage, int id);

	ArrayList<DevDTO> getDevSummary();
	ArrayList<DevDTO> getDevList(int id);

}
