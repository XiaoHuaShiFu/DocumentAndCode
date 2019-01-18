package com.qa.service;

import com.qa.bean.Question;
import com.qa.bean.QuestionFollow;
import com.qa.bean.User;
import com.qa.bean.UserFollow;
import com.qa.dao.QuestionDao;
import com.qa.dao.QuestionFollowDao;
import com.qa.dao.UserDao;
import com.qa.dao.UserFollowDao;

public class FollowService {
	
	public static void main(String[] args) {
		FollowService followService = new FollowService();
		User user = new User();
		user.setId(8);
		followService.followUser("1", 10);
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
	 * 通过被关注者id,和关注者User关注or取消关注
	 * @param followedId
	 * @param user
	 */
	public void followUser(String followedId, int userId) {
		UserFollowDao userFollowDao = new UserFollowDao();
		//获取关注信息
		UserFollow uf = new UserFollow();
		uf.setFollowerId(userId);
		uf.setUserId(Integer.parseInt(followedId));
		UserFollow userFollow = userFollowDao.query(uf);
		UserDao userDao = new UserDao();
		User user = userDao.queryUser(null, null, Integer.parseInt(followedId));
		//判断是否已经关注
		if (userFollow == null) {
			//关注
			//添加关注信息
			userFollowDao.insertUserFollow(uf);
			//被关注者关注数+1
			user.setFollower(user.getFollower() + 1);
			userDao.updateUser(user);
		} else {
			//取消关注
			//删除关注信息
			userFollowDao.deleteUserFollow(uf);
			//被关注者关注数-1
			user.setFollower(user.getFollower() - 1);
			userDao.updateUser(user);
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
	
	public String isFollow(int authorId, int userId) {
		UserFollowDao userFollowDao = new UserFollowDao();
		//获取关注信息
		UserFollow uf = new UserFollow();
		uf.setFollowerId(userId);
		uf.setUserId(authorId);
		UserFollow userFollow = userFollowDao.query(uf);
		//判断是否已经关注
		if (userFollow == null) {
			return "unfollow";
		} else {
			return "followed";
		}
	}
	
	
}
