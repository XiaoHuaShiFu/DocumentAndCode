package com.qa.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.qa.bean.Article;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class ArticleDetailServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码
		req.setCharacterEncoding("UTF-8");
		//获取值ֵ
		String phone = req.getParameter("phone");
		String password = req.getParameter("password");
		String id = req.getParameter("id");
		//设置值ֵ
		req.setAttribute("phone", phone);
		req.setAttribute("password", password);
		//获取会话
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(1200);
		if (session.isNew()) {
			session.setAttribute("phone", phone);
			session.setAttribute("password", password);
		} 
		//获取文章信息
		SearchService searchService = new SearchService();
		Article article = searchService.getArticle(id);
		//设置值
		req.setAttribute("article", article);
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
