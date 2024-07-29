package com.toyproject.scraping;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDTO {
	String title;
	String content;
	String originalpage;
	String creationdate;
	String site;
	String name;
	int id;
}
