package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import model.Account;

@SuppressWarnings("unused")
public class AccountDaoTest {
//	private Integer id;
//	private String account;
//	private Integer amount;
//	private Date createDatetime;
	public static void main(String[] args) throws SQLException {
//		Account account = new Account(2,"a",1000);
		AccountDao accountDao = new AccountDao();
		
		//正常方式操控数据库
		Date datea = new Date();
		Account account1 = null;
		for (int i = 0; i < 1000; i++) {
			account1 = accountDao.get(3);
		}
		Date dateb = new Date();
		System.out.println(dateb.getTime() - datea.getTime());
		System.out.println(account1);
		
		//dbcp方式操控数据库
		Account account2 = null;
		Date datec = new Date();
		for (int i = 0; i < 1000; i++) {
			account2 = accountDao.getByDBCP(3);
		}
		Date dated = new Date();
		System.out.println(dated.getTime() - datec.getTime());
		System.out.println(account2);
		

		
	}

}
