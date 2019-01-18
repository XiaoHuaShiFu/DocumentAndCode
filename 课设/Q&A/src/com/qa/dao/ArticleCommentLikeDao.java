package com.qa.dao;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.ArticleCommentLike;
import com.qa.db.DBAccess;

public class ArticleCommentLikeDao {
	
	public static void main(String[] args) {
		ArticleCommentLikeDao acld = new ArticleCommentLikeDao();
		ArticleCommentLike acl = new ArticleCommentLike();
		acl.setCommentId(4);
		acl.setLikerId(8);
		acld.insert(acl);
	}
	
	/**
	 * 查询数据库返回 articleCommentLike
	 * @param ArticleCommentLike
	 * @return ArticleCommentLike
	 */
	public ArticleCommentLike query(ArticleCommentLike articleCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		ArticleCommentLike acl = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			acl = sqlSession.selectOne("ArticleCommentLike.query", articleCommentLike);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return acl;
	}
	
	/**
	 * 通过ArticleCommentLike插入一条新的点赞
	 * @param ArticleCommentLike
	 */
	public void insert(ArticleCommentLike articleCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("ArticleCommentLike.insert", articleCommentLike);
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
	 * 通过articleCommentLike删除一条点赞
	 * @param ArticleCommentLike
	 */
	public void delete(ArticleCommentLike articleCommentLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("ArticleCommentLike.delete", articleCommentLike);
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
