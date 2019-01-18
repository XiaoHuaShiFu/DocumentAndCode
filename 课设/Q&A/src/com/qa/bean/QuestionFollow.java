package com.qa.bean;

public class QuestionFollow {
	
	private int id;
	private int followerId;
	private int questionId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFollowerId() {
		return followerId;
	}
	public void setFollowerId(int followerId) {
		this.followerId = followerId;
	}
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	@Override
	public String toString() {
		return "QuestionFollow [id=" + id + ", followerId=" + followerId + ", questionId=" + questionId + "]";
	}
	
	
}
