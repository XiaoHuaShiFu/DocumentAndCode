package com.qa.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qa.bean.Question;
import com.qa.bean.Search;
import com.qa.bean.User;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class MyServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码
		req.setCharacterEncoding("UTF-8");
		//获取值ֵ
		String phone = req.getParameter("phone");
		String password = req.getParameter("password");
		//设置值ֵ
		req.setAttribute("phone", phone);
		req.setAttribute("password", password);
		//处理值
		SearchService searchService = new SearchService();
		User user = null;
		
		//获取会话
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(1200);
		if (session.isNew()) {
			session.setAttribute("phone", phone);
			session.setAttribute("password", password);
		} 
		
		try {
			user = searchService.getUser(phone, " ", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			//设置值
			req.setAttribute("searchResult", "没有找到想要的信息.");
		} else {
			//设置值
			req.setAttribute("user", user);
		}
		//跳转
		req.getRequestDispatcher("/WEB-INF/jsp/front/My.jsp").forward(req, resp);
		
		
//		try {
//			user = searchService.getUsers(keyword, 0, 10);
//			searchs = searchService.getTopSearchs(0, 20);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (users == null) {
//			//设置值
//			req.setAttribute("searchResult", "没有找到想要的信息.");
//			//跳转
//			req.getRequestDispatcher("/WEB-INF/jsp/front/Search.jsp").forward(req, resp);
//		} else {
//			//设置值
//			req.setAttribute("users", users);
//			req.setAttribute("searchs", searchs);
//			//跳转
//			req.getRequestDispatcher("/WEB-INF/jsp/front/Search.jsp").forward(req, resp);
//		}
	}
	
}
