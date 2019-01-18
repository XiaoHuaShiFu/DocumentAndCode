package com.qa.service;

import com.qa.bean.User;
import com.qa.dao.UserDao;

public class SignInService {
	
	public static void main(String[] args) {
		SignInService signInService = new SignInService();
		System.out.println(signInService.signIn("15992321303", "1234567"));
	}
	
	public String signIn(String phone, String password) {
		if (phone == null || "".equals(phone.trim())) {
			return "Phone should not be empty. ";
		} else if (phone.trim().length() != 11) {
			return "The mobile number should be 11 digits. ";
		} else if (password == null || "".equals(password.trim())) {
			return "Password should not be empty. ";
		} else if (password.trim().length() < 6) {
			return "Password should be longer than 6 digits. ";
		} else if (password.trim().length() > 20) {
			return "Password length is too long. ";
		} else {
			UserDao userDao = new UserDao();
			User userInfo = userDao.queryUser(phone, "", 0);
			if (userInfo == null) {
				return "Mobile number error.";
			} else if (!userInfo.getPassword().equals(password.trim())) {
				return "Wrong password.";
			} else {
				return "SignIn.";
			}
		}
	}
	
}
