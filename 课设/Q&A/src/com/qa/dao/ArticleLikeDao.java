package com.qa.dao;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.ArticleLike;
import com.qa.db.DBAccess;

public class ArticleLikeDao {
	
	/*public static void main(String[] args) {
		ArticleLikeDao articleLikeDao = new ArticleLikeDao();
		ArticleLike articleLike = new ArticleLike();
		articleLike.setArticleId(3);
		articleLike.setLikerId(8);
		articleLikeDao.insert(articleLike);
	}*/
	
	
	/**
	 * 查询数据库返回 articleLike
	 * @param ArticleLike
	 * @return ArticleLike
	 */
	public ArticleLike query(ArticleLike articleLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		ArticleLike al = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			al = sqlSession.selectOne("ArticleLike.query", articleLike);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return al;
	}
	
	/**
	 * 通过ArticleLike插入一条新的点赞
	 * @param ArticleLike
	 */
	public void insert(ArticleLike articleLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("ArticleLike.insert", articleLike);
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
	 * 通过ArticleLike删除一条点赞
	 * @param ArticleLike
	 */
	public void delete(ArticleLike articleLike) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("ArticleLike.delete", articleLike);
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
