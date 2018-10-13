package com.qa.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.qa.bean.Article;
import com.qa.bean.Question;
import com.qa.bean.User;
import com.qa.service.FollowService;
import com.qa.service.LikeService;
import com.qa.service.SearchService;

@SuppressWarnings("serial")
public class ArticleDetailServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//编码
		req.setCharacterEncoding("UTF-8");
		//获取值
		String method = req.getParameter("method");
		//获取会话
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("user");
		if (session.isNew()) {
			req.setAttribute("signInResult", "请重新登录");
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
		if (method.equals("detail")) {
			//获取问题信息
			String id = req.getParameter("id");
			SearchService searchService = new SearchService();
			Question question = searchService.getQuestion(id);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(id, user.getId());
			//设置值
			req.setAttribute("question", question);
			req.setAttribute("isFollow", isFollow);
			req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
		} else if (method.equals("like")) {
			//点赞一个问题的评论
			String commentId = req.getParameter("commentId");
			String questionId = req.getParameter("questionId");
			LikeService likeService = new LikeService();
			likeService.likeQuestionComment(commentId, user);
			SearchService searchService = new SearchService();
			Question question = searchService.getQuestion(questionId);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(questionId, user.getId());
			//设置值
			req.setAttribute("question", question);
			req.setAttribute("isFollow", isFollow);
			req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
		} else if (method.equals("follow")) {
			//关注一个问题
			String questionId = req.getParameter("questionId");
			FollowService followeService = new FollowService();
			followeService.followQuestion(questionId, user);
			SearchService searchService = new SearchService();
			Question question = searchService.getQuestion(questionId);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(questionId, user.getId());
			//设置值
			req.setAttribute("question", question);
			req.setAttribute("isFollow", isFollow);
			req.getRequestDispatcher("/WEB-INF/jsp/front/question.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
