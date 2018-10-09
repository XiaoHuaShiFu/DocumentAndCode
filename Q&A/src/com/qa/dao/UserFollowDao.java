package com.qa.dao;


import java.util.*;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.User;
import com.qa.bean.UserFollow;
import com.qa.db.DBAccess;


public class UserFollowDao {
	
	public static void main(String[] args) {
		UserFollowDao userFollowDao = new UserFollowDao();
		System.out.println(userFollowDao.queryFollowerList(8));
	}
	
	/**
	 *  通过用户的id查询用户关注的人的id列表
	 * @param phone
	 * @param email
	 * @return
	 */
	public List<Integer> queryUserIdList(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Integer> idList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			idList = sqlSession.selectList("UserFollow.queryUserIdList", id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return idList;
	}
	
	/**
	 *  通过用户的id查询用户关注的人的User列表
	 * @param phone
	 * @param email
	 * @return
	 */
	public List<User> queryUserList(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<User> userList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			userList = sqlSession.selectList("UserFollow.queryUserList", id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return userList;
	}
	
	/**
	 *  通过用户的id查询关注用户的人的User列表
	 * @param phone
	 * @param email
	 * @return
	 */
	public List<User> queryFollowerList(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<User> followerList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			followerList = sqlSession.selectList("UserFollow.queryFollowerList", id);
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
	 * 通过关注者id和被关注者id插入一条关注信息
	 * @param user
	 */
	public void insertUserFollow(UserFollow userFollow) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("UserFollow.insertUserFollow", userFollow);
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
	 * 通过关注者id和被关注者id删除一条关注信息
	 * @param id
	 */
	public void deleteUser(UserFollow userFollow) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("UserFollow.deleteUserFollow", userFollow);
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
