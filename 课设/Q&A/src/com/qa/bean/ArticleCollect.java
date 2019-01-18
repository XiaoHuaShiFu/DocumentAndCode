package com.qa.bean;

public class ArticleCollect {

	private int id;
	private int collectorId;
	private int articleId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCollectorId() {
		return collectorId;
	}
	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	@Override
	public String toString() {
		return "ArticleCollection [id=" + id + ", collectorId=" + collectorId + ", articleId=" + articleId + "]";
	}
	
}
