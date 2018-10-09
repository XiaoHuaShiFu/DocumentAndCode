package com.qa.bean;

import java.sql.Timestamp;

public class ArticleComment {
	
	private int id;
	private int articleId;
	private int respondentId;
	private String content;
	private int like;
	private Timestamp date;
	private User respondent;
	
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
	public int getRespondentId() {
		return respondentId;
	}
	public void setRespondentId(int respondentId) {
		this.respondentId = respondentId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public User getRespondent() {
		return respondent;
	}
	public void setRespondent(User respondent) {
		this.respondent = respondent;
	}
	
	@Override
	public String toString() {
		return "ArticleComment [id=" + id + ", articleId=" + articleId + ", respondentId=" + respondentId + ", content="
				+ content + ", like=" + like + ", date=" + date + ", respondent=" + respondent + "]";
	}

}
