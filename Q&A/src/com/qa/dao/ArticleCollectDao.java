package com.qa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.Article;
import com.qa.bean.ArticleCollect;
import com.qa.db.DBAccess;

public class ArticleCollectDao {

	public static void main(String[] args) {
		ArticleCollectDao articleCollectDao = new ArticleCollectDao();
		List<Article> articleList = articleCollectDao.queryArticleList(1);
		System.out.println(articleList);
	}
	
	/**
	 * 通过用户的id查询用户收藏的文章的Article列表
	 * @param collectorId
	 * @return List
	 */
	public List<Article> queryArticleList(int collectorId) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Article> articleList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			articleList = sqlSession.selectList("ArticleCollect.queryArticleList", collectorId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return articleList;
	}
	
	/**
	 * 通过收藏者id和被收藏文章id插入一条收藏信息
	 * @param articleCollect
	 */
	public void insertArticleCollect(ArticleCollect articleCollect) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("ArticleCollect.insertArticleCollect", articleCollect);
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
	 * 通过收藏者id和被收藏文章id删除一条收藏信息
	 * @param articleCollect
	 */
	public void deleteArticleCollect(ArticleCollect articleCollect) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("ArticleCollect.deleteArticleCollect", articleCollect);
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
