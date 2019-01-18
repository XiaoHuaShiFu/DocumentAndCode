package com.qa.bean;

public class UserFollow {
	
	private int id;
	private int followerId;
	private int userId;
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
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "UserFollow [id=" + id + ", followerId=" + followerId + ", userId=" + userId + "]";
	}
	
}
