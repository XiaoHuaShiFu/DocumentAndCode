package com.qa.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import com.qa.bean.Article;
import com.qa.bean.Question;
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
		//获取值ֵ
		String phone = req.getParameter("phone");
		String password = req.getParameter("password");
		//设置值ֵ
		req.setAttribute("phone", phone);
		req.setAttribute("password", password);
		//处理值
		SignInService signInService = new SignInService();
		String signInResult = signInService.signIn(phone, password);
		if (signInResult.equals("SignIn.")) {
			//获取会话
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(1200);
			if (session.isNew()) {
				session.setAttribute("phone", phone);
				session.setAttribute("password", password);
			} 
			SearchService searchService = new SearchService();
			List<Question> questions = null;
			List<Article> articles = null;
			try {
				questions = searchService.getQuestions(" ", 0, 10, true);
				articles = searchService.getArticles(" ", 0, 10, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (questions == null && articles == null) {
				//设置值
				req.setAttribute("searchResult", "没有找到想要的信息.");
			} else {
				//设置值
				req.setAttribute("questions", questions);
				req.setAttribute("articles", articles);
			}
			//跳转
			req.getRequestDispatcher("/WEB-INF/jsp/front/Home.jsp").forward(req, resp);
		} else {
			//跳转
			req.setAttribute("signInResult", signInResult);
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
		
	}

}
