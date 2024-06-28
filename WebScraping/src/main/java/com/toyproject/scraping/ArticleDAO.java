package com.toyproject.scraping;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleDAO {
	void savearticle(String title, String finContent, String author, String originalPage, String creationdate);
	ArticleDTO jojoldulatesturl(); 
}
