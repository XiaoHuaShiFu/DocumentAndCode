package com.qa.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.qa.bean.User;
import com.qa.service.SignUpService;

@SuppressWarnings("serial")
public class SignUpServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 编码
		req.setCharacterEncoding("UTF-8");
		//获取值
		String username = req.getParameter("username");
		String phone = req.getParameter("phone");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		//设置值
		req.setAttribute("username", username);
		req.setAttribute("phone", phone);
		req.setAttribute("email", email);
		req.setAttribute("password", password);
		// 处理值
		User user = new User();
		user.setUsername(username);
		user.setPhone(phone);
		user.setEmail(email);
		user.setPassword(password);
		//跳转
		SignUpService signUpService = new SignUpService();
		String signUpResult = signUpService.signUp(user);
		if (signUpResult.equals("Registration success.")) {
			// 跳转
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		} else {
			req.setAttribute("signUpResult", signUpResult);
			// 跳转
			req.getRequestDispatcher("/WEB-INF/jsp/front/SignUp.jsp").forward(req, resp);
		}
	
	}

}
