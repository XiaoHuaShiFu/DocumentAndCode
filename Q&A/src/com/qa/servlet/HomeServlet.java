package com.qa.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qa.bean.Article;
import com.qa.bean.Question;
import com.qa.bean.User;
import com.qa.service.LikeService;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码
		req.setCharacterEncoding("UTF-8");
		//获取值
		String method = req.getParameter("method");
		if (method == null) {
			method = (String) req.getAttribute("method");
		}
		//获取会话
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(1200);
		User user = (User)session.getAttribute("user");
		if (session.isNew()) {
			req.setAttribute("signInResult", "请重新登录");
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
		if (method.equals("home")) {
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
		} else if (method.equals("detail")) {
			//获取问题信息
			String id = req.getParameter("id");
			SearchService searchService = new SearchService();
			Question question = searchService.getQuestion(id);
			//设置值
			req.setAttribute("question", question);
			req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
		} else if (method.equals("like")) {
			//点赞
			String commentId = req.getParameter("commentId");
			LikeService likeService = new LikeService();
			likeService.likeQuestionComment(commentId, user);
			//获取问题和文章
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
			req.getRequestDispatcher("/WEB-INF/jsp/front/Home.jsp").forward(req, resp);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
