package com.qa.dao;


import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.User;
import com.qa.bean.UserKeyword;
import com.qa.db.DBAccess;


public class UserDao {
	
	public static void main(String[] args) {
		UserDao userDao = new UserDao();
		UserKeyword userKeyword = new UserKeyword();
		userKeyword.setUsername("xh");
		userKeyword.setFrom(0);
		userKeyword.setTo(10);
		System.out.println(userDao.queryUser(" ", " ", 12));
	}
	
	/**
	 * 通过phone或email或id查询User
	 * @param phone
	 * @param email
	 * @param id
	 * @return User
	 */
	public User queryUser(String phone, String email, int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		User user = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			User userInfo = new User();
			userInfo.setPhone(phone);
			userInfo.setEmail(email);
			userInfo.setId(id);
			user = sqlSession.selectOne("User.queryUser", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return user;
	}
	
	/**
	 * 通过username模糊查询用户列表
	 * @param username
	 * @param from
	 * @param to
	 * @return List<User>
	 */
	public List<User> queryUserList(UserKeyword userKeyword) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<User> userList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			userList = sqlSession.selectList("User.queryUserList", userKeyword);
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
	 * 新建一个用户
	 * @param user
	 */
	public void insertUser(User user) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("User.insertUser", user);
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
	 * 删除一个用户
	 * @param id
	 */
	public void deleteUser(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("User.deleteUser", id);
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
	 * 更新一个用户的信息
	 * @param user
	 */
	public void updateUser(User user) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("User.updateUser", user);
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
