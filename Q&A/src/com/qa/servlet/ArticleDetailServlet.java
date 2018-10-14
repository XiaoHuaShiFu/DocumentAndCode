package com.qa.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.qa.bean.Article;
import com.qa.bean.User;
import com.qa.service.CommentService;
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
			//获取文章信息
			String id = req.getParameter("id");
			SearchService searchService = new SearchService();
			Article article = searchService.getArticle(id);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(article.getAuthor().getId(), user.getId());
			//设置值
			req.setAttribute("article", article);
			req.setAttribute("isFollow", isFollow);
			req.setAttribute("user", user);
			req.getRequestDispatcher("/WEB-INF/jsp/front/article.jsp").forward(req, resp);
		} else if (method.equals("like")) {
			//点赞一条评论
			String articleId = req.getParameter("articleId");
			String commentId = req.getParameter("commentId");
			LikeService likeService = new LikeService();
			likeService.likeArticleComment(commentId, user);
			SearchService searchService = new SearchService();
			Article article = searchService.getArticle(articleId);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(article.getAuthor().getId(), user.getId());
			//设置值
			req.setAttribute("article", article);
			req.setAttribute("isFollow", isFollow);
			req.getRequestDispatcher("/WEB-INF/jsp/front/article.jsp").forward(req, resp);
		} else if (method.equals("follow")) {
			//关注作者
			String authorId = req.getParameter("authorId");
			String articleId = req.getParameter("articleId");
			//关注
			FollowService followeService = new FollowService();
			followeService.followUser(authorId, user.getId());
			//获取值
			SearchService searchService = new SearchService();
			Article article = searchService.getArticle(articleId);
			FollowService followService = new FollowService();
			String isFollow = followService.isFollow(article.getAuthor().getId(), user.getId());
			//设置值
			req.setAttribute("article", article);
			req.setAttribute("isFollow", isFollow);
			req.getRequestDispatcher("/WEB-INF/jsp/front/article.jsp").forward(req, resp);
		} else if (method.equals("submit")) {
			//获取问题信息
			String content = req.getParameter("content");
			String id = req.getParameter("id");
			if (content == null) {
				SearchService searchService = new SearchService();
				Article article = searchService.getArticle(id);
				//设置值
				req.setAttribute("notice", "评论内容不能为空。");
				req.setAttribute("Article", article);
				req.getRequestDispatcher("/WEB-INF/jsp/front/article.jsp").forward(req, resp);
			} else {
				//提交评论
				CommentService commentService = new CommentService();
				commentService.addArticleComment(content, user, id);
				
				//获取文章信息
				SearchService searchService = new SearchService();
				Article article = searchService.getArticle(id);
				FollowService followService = new FollowService();
				String isFollow = followService.isFollow(id, user.getId());
				//设置值
				req.setAttribute("article", article);
				req.setAttribute("isFollow", isFollow);
				req.getRequestDispatcher("/WEB-INF/jsp/front/article.jsp").forward(req, resp);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
