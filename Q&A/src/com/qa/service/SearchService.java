package com.qa.service;

import java.util.List;

import com.qa.bean.Article;
import com.qa.bean.ArticleComment;
import com.qa.bean.ArticleCommentKeyword;
import com.qa.bean.ArticleKeyword;
import com.qa.bean.FromTo;
import com.qa.bean.Question;
import com.qa.bean.QuestionComment;
import com.qa.bean.QuestionCommentKeyword;
import com.qa.bean.QuestionKeyword;
import com.qa.bean.Search;
import com.qa.bean.User;
import com.qa.bean.UserKeyword;
import com.qa.dao.ArticleCommentDao;
import com.qa.dao.ArticleDao;
import com.qa.dao.QuestionCommentDao;
import com.qa.dao.QuestionDao;
import com.qa.dao.SearchDao;
import com.qa.dao.UserDao;
import com.qa.util.Utils;

public class SearchService {

	public static void main(String[] args) throws Exception {
		SearchService searchService = new SearchService();
		System.out.println(searchService.getQuestion("3"));
	}
	
	/**
	 * 获取热搜榜列表
	 * @param from
	 * @param to
	 * @return List<Search>
	 * @throws Exception 
	 */
	public List<Search> getTopSearchs(int from, int to) throws Exception {
		if (from < 0) {
			throw new Exception("from can not be negative. ");
		}
		if (to < 0) {
			throw new Exception("to can not be negative. ");
		}
		//若from大于to则交换from和to
		if (from > to) {
			to = Utils.swap(from, from = to);
		}
		FromTo fromTo = new FromTo();
		fromTo.setFrom(from);
		fromTo.setTo(to);
		List<Search> searchList = null;
		SearchDao searchDao = new SearchDao();
		searchList = searchDao.querySearchList(fromTo);
		return searchList;
	}
	
	/**
	 * 搜索问题列表
	 * @param title
	 * @return List<Question>
	 * @throws Exception 
	 */
	public List<Question> getQuestions(String keyword, int from, int to, boolean ramdom) throws Exception {
		//参数判断
		if (keyword == null) {
			throw new NullPointerException();
		}
		if ("".equals(keyword)) {
			throw new Exception("keyword can not be blank");
		}
		if (from < 0) {
			throw new Exception("from can not be negative. ");
		}
		if (to < 0) {
			throw new Exception("to can not be negative. ");
		}
		//若from大于to则交换from和to
		if (from > to) {
			to = Utils.swap(from, from = to);
		}
		//搜索问题
		QuestionKeyword questionKeyword = new QuestionKeyword();
		questionKeyword.setTitle(keyword);
		questionKeyword.setFrom(from);
		questionKeyword.setTo(to);
		questionKeyword.setRamdom(ramdom);
		List<Question> questions = null;
		QuestionDao questionDao = new QuestionDao();
		questions = questionDao.query(questionKeyword);
		//加载作者信息和评论列表
		QuestionCommentDao questionCommentDao = new QuestionCommentDao();
		List<QuestionComment> questionComments = null;
		QuestionCommentKeyword questionCommentKeyword = new QuestionCommentKeyword();
		questionCommentKeyword.setFrom(0);
		questionCommentKeyword.setTo(10);
		UserDao userDao = new UserDao();
		User author = null;
		for (Question question : questions) {
			//加载作者信息
			int authorId = question.getAuthorId();
			author = userDao.queryUser(" ", " ", authorId);
			question.setAuthor(author);
			//加载评论
			questionCommentKeyword.setQuestionId(question.getId());
			questionComments = questionCommentDao.query(questionCommentKeyword);
			//加载评论者
			User respondent = null;
			for (QuestionComment questionComment : questionComments) {
				respondent = userDao.queryUser(" ", " ", questionComment.getRespondentId());
				questionComment.setRespondent(respondent);
			}
			question.setComments(questionComments);
		}
		return questions;
	}
	
	/**
	 * 通过id搜索问题
	 * @param id
	 * @return Question
	 */
	public Question getQuestion(String id)  {
		//搜索问题
		Question question = null;
		QuestionDao questionDao = new QuestionDao();
		question = questionDao.query(Integer.parseInt(id));
		//加载作者信息
		UserDao userDao = new UserDao();
		User author = null;
		int authorId = question.getAuthorId();
		author = userDao.queryUser(" ", " ", authorId);
		question.setAuthor(author);
		//加载评论
		QuestionCommentDao questionCommentDao = new QuestionCommentDao();
		List<QuestionComment> questionComments = null;
		QuestionCommentKeyword questionCommentKeyword = new QuestionCommentKeyword();
		questionCommentKeyword.setQuestionId(question.getId());
		questionCommentKeyword.setFrom(0);
		questionCommentKeyword.setTo(10);
		questionComments = questionCommentDao.query(questionCommentKeyword);
		//加载评论者
		User respondent = null;
		for (QuestionComment questionComment : questionComments) {
			respondent = userDao.queryUser(" ", " ", questionComment.getRespondentId());
			questionComment.setRespondent(respondent);
		}
		question.setComments(questionComments);
		return question;
	}
	
	/**
	 * 搜索文章列表
	 * @param keyword, from, to 
	 * @return List<Article>
	 * @throws Exception 
	 */
	public List<Article> getArticles(String keyword, int from, int to, boolean ramdom) throws Exception {
		//参数判断
		if (keyword == null) {
			throw new NullPointerException();
		}
		if ("".equals(keyword)) {
			throw new Exception("keyword can not be blank");
		}
		if (from < 0) {
			throw new Exception("from can not be negative. ");
		}
		if (to < 0) {
			throw new Exception("to can not be negative. ");
		}
		//若from大于to则交换from和to
		if (from > to) {
			to = Utils.swap(from, from = to);
		}
		//搜索文章
		ArticleKeyword articleKeyword = new ArticleKeyword();
		articleKeyword.setTitle(keyword);
		articleKeyword.setFrom(from);
		articleKeyword.setTo(to);
		articleKeyword.setRamdom(ramdom);
		List<Article> articles = null;
		ArticleDao articleDao = new ArticleDao();
		articles = articleDao.queryArticleList(articleKeyword);
		//加载作者信息和评论列表
		ArticleCommentDao articleCommentDao = new ArticleCommentDao();
		List<ArticleComment> articleComments = null;
		ArticleCommentKeyword articleCommentKeyword = new ArticleCommentKeyword();
		articleCommentKeyword.setFrom(0);
		articleCommentKeyword.setTo(10);
		UserDao userDao = new UserDao();
		User author = null;
		for (Article article : articles) {
			//加载作者信息
			int authorId = article.getAuthorId();
			author = userDao.queryUser(" ", " ", authorId);
			article.setAuthor(author);
			//加载评论
			articleCommentKeyword.setArticleId(article.getId());
			articleComments = articleCommentDao.queryArticleCommentList(articleCommentKeyword);
			//加载评论者
			User respondent = null;
			for (ArticleComment articleComment : articleComments) {
				respondent = userDao.queryUser(" ", " ", articleComment.getRespondentId());
				articleComment.setRespondent(respondent);
			}
			article.setComments(articleComments);
		}
		return articles;
	}
	
	/**
	 * 通过id搜索文章
	 * @param id
	 * @return Article
	 */
	public Article getArticle(String id)  {
		//搜索文章
		Article article = null;
		ArticleDao articleDao = new ArticleDao();
		article = articleDao.queryArticle(Integer.parseInt(id));
		//加载作者信息
		UserDao userDao = new UserDao();
		User author = null;
		int authorId = article.getAuthorId();
		author = userDao.queryUser(" ", " ", authorId);
		article.setAuthor(author);
		//加载评论
		ArticleCommentDao articleCommentDao = new ArticleCommentDao();
		List<ArticleComment> articleComments = null;
		ArticleCommentKeyword articleCommentKeyword = new ArticleCommentKeyword();
		articleCommentKeyword.setArticleId(article.getId());
		articleCommentKeyword.setFrom(0);
		articleCommentKeyword.setTo(10);
		articleComments = articleCommentDao.queryArticleCommentList(articleCommentKeyword);
		//加载评论者
		User respondent = null;
		for (ArticleComment articleComment : articleComments) {
			respondent = userDao.queryUser(" ", " ", articleComment.getRespondentId());
			articleComment.setRespondent(respondent);
		}
		article.setComments(articleComments);
		return article;
	}
	
	
	/**
	 * 搜索用户列表
	 * @param keyword, from, to
	 * @return List<User>
	 * @throws Exception 
	 */
	public List<User> getUsers(String keyword, int from, int to) throws Exception {
		if (keyword == null) {
			throw new NullPointerException();
		}
		if ("".equals(keyword.trim())) {
			throw new Exception("keyword can not be blank");
		}
		if (from < 0) {
			throw new Exception("from can not be negative. ");
		}
		if (to < 0) {
			throw new Exception("to can not be negative. ");
		}
		//若from大于to则交换from和to
		if (from > to) {
			to = Utils.swap(from, from = to);
		}
		UserKeyword userKeyword = new UserKeyword();
		userKeyword.setUsername(keyword);
		userKeyword.setFrom(from);
		userKeyword.setTo(to);
		List<User> users = null;
		UserDao userDao = new UserDao();
		users = userDao.queryUserList(userKeyword);
		return users;
	}
	
	/**
	 * 搜索单个用户
	 * @param String, String, int
	 * @return User
	 */
	public User getUser(String phone, String email, int id) {
		UserDao userDao = new UserDao();
		User user = userDao.queryUser(phone, email, id);
		return user;
	}
	
	
}
