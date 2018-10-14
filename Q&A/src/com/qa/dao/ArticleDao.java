package com.qa.dao;

import java.util.*;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.Article;
import com.qa.bean.ArticleKeyword;
import com.qa.db.DBAccess;

public class ArticleDao {
	
	public static void main(String[] args) {
		ArticleDao articleDao = new ArticleDao();
		ArticleKeyword articleKeyword = new ArticleKeyword();
		articleKeyword.setTitle("");
		articleKeyword.setFrom(0);
		articleKeyword.setTo(10);
		articleKeyword.setRamdom(true);
		System.out.println(articleDao.queryArticleList(articleKeyword));
//		System.out.println(articleDao.queryArticle(1));
	}
	
	/**
	 * 查询数据库返回List<Article>
	 * @param ArticleKeyword
	 * @return List<Article>
	 */
	public List<Article> queryArticleList(ArticleKeyword articleKeyword) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Article> articleList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			articleList = sqlSession.selectList("Article.queryArticleList", articleKeyword);
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
	 * 查询数据库返回Article
	 * @param id
	 * @return Article
	 */
	public Article query(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		Article article = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			article = sqlSession.selectOne("Article.queryArticle", id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return article;
	}
	
	/**
	 * 通过文章信息Article插入一篇新文章
	 * @param article
	 */
	public void insertArticle(Article article) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("Article.insertArticle", article);
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
	 * 通过文章id删除一篇文章
	 * @param id
	 */
	public void deleteArticle(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("Article.deleteArticle", id);
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
	 * 通过文章信息Article更新文章
	 * @param article
	 */
	public void update(Article article) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("Article.updateArticle", article);
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
