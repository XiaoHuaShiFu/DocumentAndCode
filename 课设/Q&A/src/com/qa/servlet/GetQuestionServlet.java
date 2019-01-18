package com.qa.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qa.bean.Question;
import com.qa.bean.Search;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class GetQuestionServlet extends HttpServlet {
	
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
		String keyword = req.getParameter("keyword");
		//设置值ֵ
		req.setAttribute("phone", phone);
		req.setAttribute("password", password);
		req.setAttribute("keyword", keyword);
		//处理值
		SearchService searchService = new SearchService();
		List<Question> questions = null;
		List<Search> searchs = null;
		try {
			questions = searchService.getQuestions(keyword, 0, 10, false);
			searchs = searchService.getTopSearchs(0, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (questions == null) {
			//设置值
			req.setAttribute("searchResult", "没有找到想要的信息.");
			//跳转
			req.getRequestDispatcher("/WEB-INF/jsp/front/Search.jsp").forward(req, resp);
		} else {
			//设置值
			req.setAttribute("questions", questions);
			req.setAttribute("searchs", searchs);
			//跳转
			req.getRequestDispatcher("/WEB-INF/jsp/front/Search.jsp").forward(req, resp);
		}
	}
	
}
