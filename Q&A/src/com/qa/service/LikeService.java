package com.qa.service;

import com.qa.bean.QuestionComment;
import com.qa.bean.QuestionCommentLike;
import com.qa.bean.User;
import com.qa.dao.QuestionCommentDao;
import com.qa.dao.QuestionCommentLikeDao;
import com.qa.dao.UserDao;

public class LikeService {

	public static void main(String[] args) {
		LikeService likeService = new LikeService();
		UserDao userDao = new UserDao();
		User user = userDao.queryUser(null, null, 1);
		likeService.likeQuestionComment("5", user);
	}
	
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
	
}
