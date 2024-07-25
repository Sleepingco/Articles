package com.toyproject.scraping;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleDAO {
	ArrayList<ArticleDTO> getArticleList();
	void saveTistoryArticle(String title, String finContent, String author, String originalPage, String creationdate, String blogName, int id);
	void saveLinkedinArticle(String author, String Content, int id, String originalpage, String siteName); 
	void saveCareelyArticle(String title, String content, String author, String site, int id);
	ArticleDTO getNewestUrl(int testId,String site);
}
