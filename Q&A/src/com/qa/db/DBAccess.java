package com.qa.db;

import java.io.*;

import org.apache.ibatis.io.*;
import org.apache.ibatis.session.*;

/**
 * �������ݿ�
 */
public class DBAccess {
	
	public SqlSession getSqlSession() throws IOException {
		Reader reader = Resources.getResourceAsReader("com/qa/config/Configuration.xml");
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		SqlSession sqlSession = sqlSessionFactory.openSession();
		return sqlSession;
	}
}