package com.qa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.ArticleComment;
import com.qa.bean.ArticleCommentKeyword;
import com.qa.db.DBAccess;

public class ArticleCommentDao {
	
	public static void main(String[] args) {
		ArticleCommentDao articleCommentDao = new ArticleCommentDao();
		ArticleCommentKeyword articleCommentKeyword = new ArticleCommentKeyword();
		articleCommentKeyword.setFrom(0);
		articleCommentKeyword.setTo(10);
		articleCommentKeyword.setArticleId(3);
		List<ArticleComment> articleComments = 
				articleCommentDao.queryArticleCommentList(articleCommentKeyword);
		System.out.println(articleComments);
		ArticleComment articleComment = new ArticleComment();
		articleComment.setId(5);
		articleComment.setArticleId(3);
		articleComment.setRespondentId(12);
		articleComment.setContent("黄俊豪到此二游");
//		articleCommentDao.insertArticleComment(articleComment);
		articleCommentDao.updateArticleComment(articleComment);
	}
	
	/**
	 * 查询数据库返回List<ArticleComment>
	 * @param ArticleCommentKeyword
	 * @return List<ArticleComment>
	 */
	public List<ArticleComment> queryArticleCommentList(ArticleCommentKeyword articleCommentKeyword) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<ArticleComment> articleCommentList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			articleCommentList = sqlSession.selectList("ArticleComment.queryArticleCommentList", articleCommentKeyword);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return articleCommentList;
	}
	
	/**
	 * 通过文章评论ArticleComment插入一条新的评论
	 * @param ArticleComment
	 */
	public void insertArticleComment(ArticleComment articleComment) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("ArticleComment.insertArticleComment", articleComment);
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
	 * 通过评论的id删除一条评论
	 * @param id
	 */
	public void deleteArticleComment(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("ArticleComment.deleteArticleComment", id);
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
	 * 通过评论信息ArticleComment更新评论
	 * @param ArticleComment
	 */
	public void updateArticleComment(ArticleComment articleComment) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("ArticleComment.updateArticleComment", articleComment);
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
