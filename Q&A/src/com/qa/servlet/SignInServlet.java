package com.qa.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qa.bean.User;
import com.qa.service.SearchService;
import com.qa.service.SignInService;

@SuppressWarnings("serial")
public class SignInServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码
		req.setCharacterEncoding("UTF-8");
		//获取值
		String phone = req.getParameter("phone");
		String password = req.getParameter("password");
		//处理值
		SignInService signInService = new SignInService();
		String signInResult = signInService.signIn(phone, password);
		if (signInResult.equals("SignIn.")) {
			//设置值
			req.setAttribute("method", "home");
			//获取会话
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(1200);
			SearchService searchService = new SearchService();
			User user = searchService.getUser(phone, null, 0);
			//设置值
			session.setAttribute("user", user);
			req.getRequestDispatcher("Home").forward(req, resp);
		} else {
			//设置值
			req.setAttribute("phone", phone);
			req.setAttribute("password", password);
			//跳转
			req.setAttribute("signInResult", signInResult);
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
		
	}

}
