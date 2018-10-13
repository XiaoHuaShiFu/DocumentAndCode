package com.qa.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qa.bean.Question;
import com.qa.bean.User;
import com.qa.service.CommentService;
import com.qa.service.FollowService;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class QuestionCommentServlet extends HttpServlet {

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
		User user = (User)session.getAttribute("user");
		if (session.isNew()) {
			req.setAttribute("signInResult", "请重新登录");
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
		if (method.equals("comment")) {
			//获取问题信息
			String id = req.getParameter("id");
			SearchService searchService = new SearchService();
			Question question = searchService.getQuestion(id);
			//设置值
			req.setAttribute("question", question);
			req.getRequestDispatcher("/WEB-INF/jsp/front/answer.jsp").forward(req, resp);
		} else if (method.equals("submit")) {
			//获取问题信息
			String content = req.getParameter("content");
			String id = req.getParameter("id");
			if (content == null) {
				SearchService searchService = new SearchService();
				Question question = searchService.getQuestion(id);
				//设置值
				req.setAttribute("notice", "评论内容不能为空。");
				req.setAttribute("question", question);
				req.getRequestDispatcher("/WEB-INF/jsp/front/answer.jsp").forward(req, resp);
			} else {
				//提交评论
				CommentService commentService = new CommentService();
				commentService.addQuestionComment(content, user, id);
				
				//应该转到QuestionDetailServlet
				//获取问题信息
				SearchService searchService = new SearchService();
				Question question = searchService.getQuestion(id);
				FollowService followService = new FollowService();
				String isFollow = followService.isFollow(id, user.getId());
				//设置值
				req.setAttribute("question", question);
				req.setAttribute("isFollow", isFollow);
				req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
			}
		} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
