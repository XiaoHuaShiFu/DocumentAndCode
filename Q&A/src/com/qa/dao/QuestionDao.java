package com.qa.dao;


import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.Question;
import com.qa.bean.QuestionKeyword;
import com.qa.db.DBAccess;

public class QuestionDao {
	
	public static void main(String[] args) {
		/*QuestionDao questionDao = new QuestionDao();
		QuestionKeyword questionKeyword = new QuestionKeyword();
		questionKeyword.setTitle("");
		questionKeyword.setFrom(0);
		questionKeyword.setTo(10);
		questionKeyword.setRamdom(true);
//		Question question = (Question) questionDao.query("这是一篇文章", 1 , 1);
		List<Question> questionList = questionDao.query(questionKeyword);
		System.out.println(questionDao.query(3));*/
	}
	
	/**
	 * 查询数据库返回questions
	 * @param title
	 * @param quesitonId
	 * @param authorId
	 * @return List<Question>
	 */
	public List<Question> query(String title, int quesitonId, int authorId) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Question> questionList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			Question questionInfo = new Question();
			questionInfo.setTitle(title);
			questionInfo.setId(quesitonId);
			questionInfo.setAuthorId(authorId);
			questionList = sqlSession.selectList("Question.query", questionInfo);
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
	 * 查询数据库返回questionList
	 * @param QuestionKeyword
	 * @return List<Question>
	 */
	public List<Question> query(QuestionKeyword questionKeyword) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Question> questionList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			questionList = sqlSession.selectList("Question.queryList", questionKeyword);
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
	 * 查询数据库返回question
	 * @param id
	 * @return Question
	 */
	public Question query(int id) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		Question question = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			question = sqlSession.selectOne("Question.queryOne", id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return question;
	}
	
	/**
	 * 通过问题的信息插入一个新问题
	 * @param question
	 */
	public void insert(Question question) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("Question.insert", question);
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
	 * 通过问题的id删除一个问题
	 * @param questionId
	 */
	public void delete(int questionId) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("Question.delete", questionId);
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
	 * 通过问题的信息更新问题
	 * @param question
	 */
	public void update(Question question) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("Question.update", question);
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
