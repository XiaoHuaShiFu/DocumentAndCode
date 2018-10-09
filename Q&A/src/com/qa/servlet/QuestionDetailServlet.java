package com.qa.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qa.bean.Question;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class QuestionDetailServlet extends HttpServlet {

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
		//获取问题信息
		SearchService searchService = new SearchService();
		Question question = searchService.getQuestion(id);
		//设置值
		req.setAttribute("question", question);
		req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
