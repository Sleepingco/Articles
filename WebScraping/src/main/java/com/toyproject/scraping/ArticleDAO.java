package com.toyproject.scraping;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleDAO {
	void saveTistoryArticle(String title, String finContent, String author, String originalPage, String creationdate, String blogName, String name);
	void saveLinkedinArticle(String author, String Content, String name, String originalpage);
	ArticleDTO jojolduLatestUrl(); 
	ArrayList<ArticleDTO> getArticleList();

}
