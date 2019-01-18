package com.qa.bean;

import java.util.List;
import java.sql.Timestamp;

public class Article {
	
	private int id;
	private int authorId;
	private int collection;
	private int click;
	private String title;
	private String content;
	private Timestamp date;
	private int like;
	private int comment;
	
	private User author;
	private List<ArticleComment> comments;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getCollection() {
		return collection;
	}
	public void setCollection(int collection) {
		this.collection = collection;
	}
	public int getClick() {
		return click;
	}
	public void setClick(int click) {
		this.click = click;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public List<ArticleComment> getComments() {
		return comments;
	}
	public void setComments(List<ArticleComment> comments) {
		this.comments = comments;
	}
	
	@Override
	public String toString() {
		return "Article [id=" + id + ", authorId=" + authorId + ", collection=" + collection + ", click=" + click
				+ ", title=" + title + ", content=" + content + ", date=" + date + ", like=" + like + ", comment="
				+ comment + ", author=" + author + ", comments=" + comments + "]";
	}
	
}
