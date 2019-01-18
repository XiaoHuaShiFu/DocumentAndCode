package com.qa.dao;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.QuestionCommentLike;
import com.qa.db.DBAccess;

public class QuestionCommentLikeDao {
	

	/**
	 * 查询数据库返回 questionCommentLike
	 * @param QuestionCommentLike
	 * @return QuestionCommentLike
	 */
	public QuestionCommentLike query(QuestionCommentLike questionCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		QuestionCommentLike qcl = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			qcl = sqlSession.selectOne("QuestionCommentLike.query", questionCommentLike);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return qcl;
	}
	
	/**
	 * 通过QuestionCommentLike插入一条新的点赞
	 * @param QuestionCommentLike
	 */
	public void insert(QuestionCommentLike questionCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("QuestionCommentLike.insert", questionCommentLike);
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
	 * 通过questionCommentLike删除一条点赞
	 * @param QuestionCommentLike
	 */
	public void delete(QuestionCommentLike questionCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("QuestionCommentLike.delete", questionCommentLike);
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
