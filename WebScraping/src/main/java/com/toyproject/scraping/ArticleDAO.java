package com.toyproject.scraping;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONArray;

@Mapper
public interface ArticleDAO {
	public ArrayList<ArticleDTO> getArticleList(
	        @Param("site") List<String> siteList, 
//	        @Param("title") String title, 
//	        @Param("content") String content, 
	        @Param("filter") String filter,
	        @Param("limit") int limit, 
	        @Param("offset") int offset, 
	        @Param("idList") List<Integer> idList);

	
	void saveArticle(String title, String content,String originalpage,String date, String site, int id);
	void updateArticle(String title, String content, String date, String originalPage, int id);
	void saveLinkedinArticle(String content, String originalpage,String date, String siteName,int id); 
	void updateLinkedinArticle(String content, String date, String originalPage, int id);
	
	ArticleDTO getNewestUrl(int testId,String site);
	ArticleDTO findArticleByIdentifier(String originalPage, int id);

	ArrayList<DevDTO> getDevSummary();
	ArrayList<DevDTO> getDevList(int id);

}
