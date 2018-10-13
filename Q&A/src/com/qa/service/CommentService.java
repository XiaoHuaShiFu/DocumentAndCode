package com.qa.service;

import com.qa.bean.Question;
import com.qa.bean.QuestionComment;
import com.qa.bean.User;
import com.qa.dao.QuestionCommentDao;
import com.qa.dao.QuestionDao;

public class CommentService {
	
	public static void main(String[] args) {
		User user = new User();
		user.setId(8);
		CommentService commentService = new CommentService();
		commentService.addQuestionComment("哈哈哈哈我来测试了", user, "1");
	}
	
	/**
	 * 插入一条问题的评论,并使评论数+1
	 * @param content
	 * @param user
	 * @param questionId
	 */
	public void addQuestionComment(String content, User user, String questionId) {
		//插入评论
		QuestionCommentDao questionCommentDao = new QuestionCommentDao();
		QuestionComment questionComment = new QuestionComment();
		questionComment.setContent(content);
		questionComment.setRespondentId(user.getId());
		questionComment.setQuestionId(Integer.parseInt(questionId));
		questionCommentDao.insert(questionComment);
		//更新问题信息(使评论数+1)
		QuestionDao questionDao = new QuestionDao();
		Question question = questionDao.query(Integer.parseInt(questionId));
		question.setComment(question.getComment() + 1);
		questionDao.update(question);
	}
	
}
