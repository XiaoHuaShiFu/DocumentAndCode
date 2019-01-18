package com.qa.service;

import com.qa.bean.Article;
import com.qa.bean.ArticleComment;
import com.qa.bean.ArticleCommentLike;
import com.qa.bean.ArticleLike;
import com.qa.bean.QuestionComment;
import com.qa.bean.QuestionCommentLike;
import com.qa.bean.User;
import com.qa.dao.ArticleCommentDao;
import com.qa.dao.ArticleCommentLikeDao;
import com.qa.dao.ArticleDao;
import com.qa.dao.ArticleLikeDao;
import com.qa.dao.QuestionCommentDao;
import com.qa.dao.QuestionCommentLikeDao;

public class LikeService {

	/*public static void main(String[] args) {
		LikeService likeService = new LikeService();
		UserDao userDao = new UserDao();
		User user = userDao.queryUser(null, null, 1);
		likeService.likeArticle("3", 17);
	}*/
	
	/**
	 * 点击点赞按钮，如果已点赞，则删除点赞信息，并使此条评论的点赞数-1
	 * 否则添加点赞信息，并并使此条评论的点赞数+1
	 * @param commentId
	 * @param user
	 */
	public void likeQuestionComment(String commentId, User user) {
		QuestionCommentLike questionCommentLike = new QuestionCommentLike();
		questionCommentLike.setCommentId(Integer.parseInt(commentId));
		questionCommentLike.setLikerId(user.getId());
		QuestionCommentLikeDao questionCommentLikeDao = new QuestionCommentLikeDao();
		QuestionCommentDao questionCommentDao = new QuestionCommentDao();
		QuestionComment questionComment = questionCommentDao.query(Integer.parseInt(commentId));
		if (questionCommentLikeDao.query(questionCommentLike) == null) {
			questionCommentLikeDao.insert(questionCommentLike);
			questionComment.setLike(questionComment.getLike()+1);
			questionCommentDao.update(questionComment);
		} else {
			questionCommentLikeDao.delete(questionCommentLike);
			questionComment.setLike(questionComment.getLike()-1);
			questionCommentDao.update(questionComment);
			
		}
	}
	
	/**
	 * 点击点赞按钮，如果已点赞，则删除点赞信息，并使此条评论的点赞数-1
	 * 否则添加点赞信息，并并使此条评论的点赞数+1
	 * @param commentId
	 * @param user
	 */
	public void likeArticleComment(String commentId, User user) {
		ArticleCommentLike articleCommentLike = new ArticleCommentLike();
		articleCommentLike.setCommentId(Integer.parseInt(commentId));
		articleCommentLike.setLikerId(user.getId());
		ArticleCommentLikeDao articleCommentLikeDao = new ArticleCommentLikeDao();
		ArticleCommentDao articleCommentDao = new ArticleCommentDao();
		ArticleComment articleComment = articleCommentDao.query(Integer.parseInt(commentId));
		if (articleCommentLikeDao.query(articleCommentLike) == null) {
			articleCommentLikeDao.insert(articleCommentLike);
			articleComment.setLike(articleComment.getLike()+1);
			articleCommentDao.update(articleComment);
		} else {
			articleCommentLikeDao.delete(articleCommentLike);
			articleComment.setLike(articleComment.getLike()-1);
			articleCommentDao.update(articleComment);
		}
	}
	
	/**
	 * 点击点赞按钮，如果已点赞，则删除点赞信息，并使此篇文章的点赞数-1
	 * 否则添加点赞信息，并并使此篇文章的点赞数+1
	 * @param articleId
	 * @param userId
	 */
	public void likeArticle(String articleId, int userId) {
		ArticleLike articleLike = new ArticleLike();
		articleLike.setArticleId(Integer.parseInt(articleId));
		articleLike.setLikerId(userId);
		ArticleLikeDao articleLikeDao = new ArticleLikeDao();
		ArticleDao articleDao = new ArticleDao();
		Article article = articleDao.query(Integer.parseInt(articleId));
		if (articleLikeDao.query(articleLike) == null) {
			articleLikeDao.insert(articleLike);
			article.setLike(article.getLike()+1);
			articleDao.update(article);
		} else {
			articleLikeDao.delete(articleLike);
			article.setLike(article.getLike()-1);
			articleDao.update(article);
		}
	}
	
}
