package com.qa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.Question;
import com.qa.bean.QuestionFollow;
import com.qa.bean.User;
import com.qa.db.DBAccess;

public class QuestionFollowDao {

	public static void main(String[] args) {
//		QuestionFollowDao questionFollowDao = new QuestionFollowDao();
	
	}
	
	/**
	 * 通过用户的id查询用户关注的问题的Question列表
	 * @param userId
	 * @return List
	 */
	public List<Question> queryQuestionList(int userId) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Question> questionList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			questionList = sqlSession.selectList("QuestionFollow.queryQuestionList", userId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return questionList;
	}
	
	/**
	 * 通过问题的id查询关注问题的人的User列表
	 * @param questionId
	 * @return List
	 */
	public List<User> queryFollowerList(int questionId) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<User> followerList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			followerList = sqlSession.selectList("QuestionFollow.queryFollowerList", questionId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return followerList;
	}
	
	/**
	 * 通过关注者id和被关注问题id插入一条关注信息
	 * @param questionFollow
	 */
	public void insertQuestionFollow(QuestionFollow questionFollow) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("QuestionFollow.insertQuestionFollow", questionFollow);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}
	
	/**
	 * 通过关注者id和被关注问题id删除一条关注信息
	 * @param questionFollow
	 */
	public void deleteQuestionFollow(QuestionFollow questionFollow) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("QuestionFollow.deleteQuestionFollow", questionFollow);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}

}
