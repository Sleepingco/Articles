package com.toyproject.scraping;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleDAO {
	ArrayList<ArticleDTO> getArticleList();
	
	void saveTistoryArticle(String title, String finContent, String author, String originalPage, String creationdate, String blogName, int id);
	void saveLinkedinArticle(String content, String originalpage,String date, String siteName,int id); 
	void saveCareelyArticle(String title, String content,String originalpage,String date, String site, int id);
	
	void updateCareelyArticle(String title, String content, String date, String originalPage, int id);
	ArticleDTO getNewestUrl(int testId,String site);
	ArticleDTO findArticleByIdentifier(String originalPage, int id);
	void updateLinkedinArticle(String content, String date, String siteName, int id);
	
}
