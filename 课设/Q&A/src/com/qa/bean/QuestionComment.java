package com.qa.bean;

import java.sql.Timestamp;

public class QuestionComment {
	
	private int id;
	private int questionId;
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
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
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
		return "QuestionComment [id=" + id + ", questionId=" + questionId + ", respondentId=" + respondentId
				+ ", content=" + content + ", like=" + like + ", date=" + date + ", respondent=" + respondent + "]";
	}
	
}
