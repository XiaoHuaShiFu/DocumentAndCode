package com.qa.bean;

public class User {
	
	private int id;
	private String username;
	private String password;
	private String phone;
	private String email;
	private String sex;
	private String introduction;
	private int follower;
	private int follow;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String Email() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getIntroduction() {
		return introduction;
	}
	public String getPhone() {
		return phone;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public int getFollower() {
		return follower;
	}
	public void setFollower(int follower) {
		this.follower = follower;
	}
	public int getFollow() {
		return follow;
	}
	public void setFollow(int follow) {
		this.follow = follow;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", phone=" + phone + ", email="
				+ email + ", sex=" + sex + ", introduction=" + introduction + ", follower=" + follower + ", follow="
				+ follow + "]";
	}
	
}
