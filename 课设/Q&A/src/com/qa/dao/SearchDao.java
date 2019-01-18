package com.qa.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.qa.bean.FromTo;
import com.qa.bean.Search;
import com.qa.db.DBAccess;

public class SearchDao {

	public static void main(String[] args) {
		SearchDao searchDao = new SearchDao();
		Search search = new Search();
		search.setId(11);
		searchDao.updateSearch(search);
		for (int i = 0; i < 10 ; i++) {
			FromTo fromTo = new FromTo();
			fromTo.setFrom(0);
			fromTo.setTo(10);
			System.out.println(searchDao.querySearchList(fromTo).get(i));
		}
		
	}
	
	/**
	 * 搜索搜索数前10的关键词
	 * @return List<SearchDao>
	 */
	public List<Search> querySearchList(FromTo fromTo) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		List<Search> searchList = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			searchList = sqlSession.selectList("Search.querySearchList", fromTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null ) {
				sqlSession.close();
			}
		}
		return searchList;
	}
	
	/**
	 * 写入一条新的搜索信息
	 * @param String
	 */
	public void insertSearch(Search search) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.insert("Search.insertSearch", search);
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
	 * 删除一条搜索关键字
	 * @param search
	 */
	public void deleteUser(Search search) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.delete("Search.deleteSearch", search);
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
	 * 使搜索数+1
	 * @param search
	 */
	public void updateSearch(Search search) {
		DBAccess dbAccess = new DBAccess();
		SqlSession sqlSession = null;
		try {
			sqlSession = dbAccess.getSqlSession();
			sqlSession.update("Search.updateSearch", search);
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
