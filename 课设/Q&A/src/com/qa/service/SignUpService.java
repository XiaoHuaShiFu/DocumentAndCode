package com.qa.service;

import com.qa.bean.User;
import com.qa.dao.UserDao;

public class SignUpService {

	public static void main(String[] args) {
		SignUpService signUpService = new SignUpService();
		User user = new User();
		user.setEmail("827032773@qq.com");
		user.setPassword("12345");
		user.setPhone("15992321103");
		user.setUsername("tttt");
		System.out.println(signUpService.signUp(user));
	}
	
	
	public String signUp(User user) {
		if (user.getUsername() == null || "".equals(user.getUsername().trim())) {
			return "Username should not be empty. ";
		} else if (user.getUsername().length() < 2) {
			return "Username length is too short. ";
		} else if (user.getUsername().length() > 20) {
			return "Username length is too long. ";
		} else if (user.getEmail() == null || "".equals(user.getEmail().trim())) {
			return "Email should not be empty. ";
		} else if (user.getEmail().trim().length() > 50) {
			return "Email length is too long. ";
		} else if (user.getPhone() == null || "".equals(user.getPhone().trim())) {
			return "Phone should not be empty. ";
		} else if (user.getPhone().trim().length() != 11) {
			return "The mobile number should be 11 digits. ";
		} else if (user.getPassword() == null || "".equals(user.getPassword().trim())) {
			return "Password should not be empty. ";
		} else if (user.getPassword().trim().length() < 6) {
			return "Password should be longer than 6 digits. ";
		} else if (user.getPassword().trim().length() > 20) {
			return "Password length is too long. ";
		} else {
			UserDao userDao = new UserDao();
			User userInfo = userDao.queryUser(user.getPhone(), user.getEmail(), 0);
			if (userInfo != null && userInfo.getPhone().equals(user.getPhone().trim())) {
				return "Mobile number already exists.";
			} else if (userInfo != null && userInfo.getEmail().equals(user.getEmail().trim())) {
				return "Email already exists.";
			} else {
				userDao.insertUser(user);
				return "Registration success.";
			}
		}
	}

}
