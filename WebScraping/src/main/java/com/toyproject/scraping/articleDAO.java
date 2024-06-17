package com.toyproject.scraping;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface articleDAO {
	void savearticle(String title, String content, String author, String origianlpage, String creationdate);
}
