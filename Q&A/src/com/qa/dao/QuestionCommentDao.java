package com.qa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.QuestionComment;
import com.qa.bean.QuestionCommentKeyword;
import com.qa.db.DBAccess;

public class QuestionCommentDao {
	
	public static void main(String[] args) {
		QuestionCommentDao questionCommentDao = new QuestionCommentDao();
		QuestionCommentKeyword questionCommentKeyword = new QuestionCommentKeyword();
		questionCommentKeyword.setFrom(0);
		questionCommentKeyword.setTo(10);
		questionCommentKeyword.setQuestionId(1);
		List<QuestionComment> questionComments = 
				questionCommentDao.query(questionCommentKeyword);
		System.out.println(questionComments);
		QuestionComment questionComment = new QuestionComment();
//		questionComment.setId(3);
		questionComment.setQuestionId(3);
		questionComment.setRespondentId(8);
		questionComment.setContent("这几个sb黄俊豪在干嘛  hahahahahahhhh");
//		questionCommentDao.delete(7);
//		questionCommentDao.update(questionComment);
	}
	
	/**
	 * 查询数据库返回List<QuestionComment>
	 * @param QuestionCommentKeyword
	 * @return List<QuestionComment>
	 */
	public List<QuestionComment> query(QuestionCommentKeyword questionCommentKeyword) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<QuestionComment> questionCommentList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			questionCommentList = sqlSession.selectList("QuestionComment.queryList", questionCommentKeyword);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return questionCommentList;
	}
	
	/**
	 * 通过文章评论QuestionComment插入一条新的评论
	 * @param QuestionComment
	 */
	public void insert(QuestionComment questionComment) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("QuestionComment.insert", questionComment);
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
	public void delete(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("QuestionComment.delete", id);
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
	 * 通过评论信息QuestionComment更新评论
	 * @param QuestionComment
	 */
	public void update(QuestionComment questionComment) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("QuestionComment.update", questionComment);
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
