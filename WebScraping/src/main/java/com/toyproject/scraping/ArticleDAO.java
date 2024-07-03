package com.toyproject.scraping;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleDAO {
	void savearticle(String title, String finContent, String author, String originalPage, String creationdate, String blogName, String name);
	ArticleDTO jojolduLatestUrl(); 
	ArrayList<ArticleDTO> getArticleList();

}
