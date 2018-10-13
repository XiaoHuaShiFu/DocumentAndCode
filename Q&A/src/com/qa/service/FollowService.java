package com.qa.service;

import com.qa.bean.Question;
import com.qa.bean.QuestionFollow;
import com.qa.bean.User;
import com.qa.dao.QuestionDao;
import com.qa.dao.QuestionFollowDao;

public class FollowService {
	
	public static void main(String[] args) {
		FollowService followService = new FollowService();
		User user = new User();
		user.setId(8);
		System.out.println(followService.isFollow("3",7));
	}
	
	/**
	 * 通过questionId,和关注者User关注or取消关注
	 * @param questionId
	 * @param user
	 */
	public void followQuestion(String questionId, User user) {
		QuestionFollowDao questionFollowDao = new QuestionFollowDao();
		//获取关注信息
		QuestionFollow qf = new QuestionFollow();
		qf.setFollowerId(user.getId());
		qf.setQuestionId(Integer.parseInt(questionId));
		QuestionFollow questionFollow = questionFollowDao.query(qf);
		QuestionDao questionDao = new QuestionDao();
		Question question = questionDao.query(Integer.parseInt(questionId));
		//判断是否已经关注
		if (questionFollow == null) {
			//关注
			//添加关注信息
			questionFollowDao.insertQuestionFollow(qf);
			//问题关注数+1
			question.setFollow(question.getFollow() + 1);
			questionDao.update(question);
		} else {
			//取消关注
			//删除关注信息
			questionFollowDao.deleteQuestionFollow(qf);
			//问题关注数-1
			question.setFollow(question.getFollow() - 1);
			questionDao.update(question);
		}
	}
	
	/**
	 * 通过questionId和userId判断用户是否已经关注问题
	 * @param questionId，userId
	 * @param "followed"，"unfollow"
	 */
	public String isFollow(String questionId, int userId) {
		QuestionFollowDao questionFollowDao = new QuestionFollowDao();
		//获取关注信息
		QuestionFollow qf = new QuestionFollow();
		qf.setFollowerId(userId);
		qf.setQuestionId(Integer.parseInt(questionId));
		QuestionFollow questionFollow = questionFollowDao.query(qf);
		//判断是否已经关注
		if (questionFollow == null) {
			return "unfollow";
		} else {
			return "followed";
		}
	}
	
}
