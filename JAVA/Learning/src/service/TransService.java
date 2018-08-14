package service;

import java.sql.Connection;
import java.sql.SQLException;

import dao.*;
import db.DBUtil;
import model.*;

public class TransService {
	
	public TransService() {
	}
	
	public String UnsafeTrans(Account from, Account to, double amount) throws SQLException {
		
		AccountDao accountDao = new AccountDao();
		TransInfoDao transInfoDao = new TransInfoDao();
		
		if (from.getAmount()  < amount) {
			return "Failed: The amount of the transferred account is not enough.";
		} else {
			from.setAmount(from.getAmount() - amount);
			to.setAmount(to.getAmount() + amount);
			accountDao.update(from);
			
			String test = null;
			test.split("1");
			
			accountDao.update(to);
			
			TransInfo transInfo = new TransInfo(from.getId(), from.getAccount(), to.getId(), to.getAccount(), amount);
			transInfoDao.add(transInfo);
			return "Successful transfer.";
		}
	}
	
	public String trans(Account from, Account to, double amount) throws SQLException {
		Connection connection = DBUtil.getConnection();
		connection.setAutoCommit(false);
		
		try {
			AccountDao accountDao = new AccountDao();
			TransInfoDao transInfoDao = new TransInfoDao();
			
			//如果钱不够则报错
			if (from.getAmount()  < amount) {
				return "Failed: The amount of the transferred account is not enough.";
			} else {
				from.setAmount(from.getAmount() - amount);
				to.setAmount(to.getAmount() + amount);
				accountDao.update(from);
				accountDao.update(to);
				
				//添加转账信息
				TransInfo transInfo = new TransInfo(from.getId(), from.getAccount(), to.getId(), to.getAccount(), amount);
				transInfoDao.add(transInfo);
				
				//手动提交
				connection.commit();
				return "Successful transfer.";
			}
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			return "Transfer failed, unknown error occurred.";
		}
	}
	
}
