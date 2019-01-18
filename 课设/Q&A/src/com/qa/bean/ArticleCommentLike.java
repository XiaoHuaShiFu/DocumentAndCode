package com.qa.bean;

import java.sql.Timestamp;

public class ArticleCommentLike {
	
	private int id;
	private int commentId;
	private int likerId;
	private Timestamp date;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
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
		return "ArticleCommentLike [id=" + id + ", commentId=" + commentId + ", likerId=" + likerId + ", date=" + date
				+ "]";
	}
	
}
