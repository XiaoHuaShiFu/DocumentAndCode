package com.qa.bean;

import java.sql.Timestamp;

public class ArticleLike {
	
	private int id;
	private int articleId;
	private int likerId;
	private Timestamp date;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public int getLikerId() {
		return likerId;
	}
	public void setLikerId(int likerId) {
		this.likerId = likerId;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "ArticleLike [id=" + id + ", articleId=" + articleId + ", likerId=" + likerId + ", date=" + date + "]";
	}

}
