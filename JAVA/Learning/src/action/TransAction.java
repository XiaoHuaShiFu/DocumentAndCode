package action;

import java.sql.SQLException;

import dao.*;
import model.Account;
import service.TransService;

public class TransAction {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		TransService transService = new TransService();
		
		AccountDao accountDao = new AccountDao();
		
		Account from = accountDao.get(2);
		Account to = accountDao.get(1);
		
		double amount = 100;
		
		System.out.println(transService.trans(from, to, amount));
		
	}

}
