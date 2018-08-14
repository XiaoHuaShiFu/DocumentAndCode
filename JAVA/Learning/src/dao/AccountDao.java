package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.*;
import model.Account;

public class AccountDao {
	
	private Connection connection;
	private Connection connectionDBCP;
	
	/**
	 * 初始化AccountDao对象
	 */
	public AccountDao(){
		super();
		this.connection = DBUtil.getConnection();
		this.connectionDBCP = DBCPUtil.getConnection();
	}
	
	public void add(Account account) throws SQLException {
		String sql = "" + 
				" INSERT INTO account_info" + 
				" (account, amount)" + 
				" VALUES(?, ?);";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, account.getAccount());
		preparedStatement.setDouble(2, account.getAmount());
		preparedStatement.execute();
	}
	
	/**
	 * 删除账户
	 * @param id
	 * @throws SQLException
	 */
	public void delete(Integer id) throws SQLException {
		String sql = "" + 
				" DELETE FROM account_info" + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		preparedStatement.execute();
	}
	
	/**
	 * 更新账户
	 * @param account
	 * @throws SQLException
	 */
	public void update(Account account) throws SQLException {
		String sql = "" + 
				" UPDATE account_info" + 
				" SET account = ? , amount = ? " + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, account.getAccount());
		preparedStatement.setDouble(2, account.getAmount());
		preparedStatement.setInt(3, account.getId());
		preparedStatement.execute();
	}
	
	/**
	 * 通过id查询账户
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Account get(Integer id) throws SQLException {
		String sql = "" + 
				" SELECT account, amount, create_datetime FROM account_info" + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setDouble(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		Account account = new Account();
		while (resultSet.next()) {
			account.setId(id);
			account.setAccount(resultSet.getString("account"));
			account.setAmount(resultSet.getDouble("amount"));
			account.setCreateDatetime(resultSet.getDate("create_datetime"));
		}
		return account;
	}
	
	/**
	 * 通过DBCP连接池的方式连接速度较快
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Account getByDBCP(Integer id) throws SQLException {
		String sql = "" + 
				" SELECT account, amount, create_datetime FROM account_info" + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connectionDBCP.prepareStatement(sql);
		preparedStatement.setDouble(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		Account account = new Account();
		while (resultSet.next()) {
			account.setId(id);
			account.setAccount(resultSet.getString("account"));
			account.setAmount(resultSet.getDouble("amount"));
			account.setCreateDatetime(resultSet.getDate("create_datetime"));
		}
		return account;
	}
	
	/**
	 * 任意个数任意参数查询
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Account> query(List<Map<String, Object>> params) throws SQLException {
		StringBuilder sql = new StringBuilder("" + 
				" SELECT id, account, amount, create_datetime FROM account_info" + 
				" WHERE ");
		if (params != null && params.size() > 0) {
			if (params.get(0).get("logic") == "and") {
				sql.append("1 = 1");
			} else if (params.get(0).get("logic") == "or") {
				sql.append("1 = 0");
			} 
			for (Map<String, Object> map : params) {
				sql.append(" " + map.get("logic") + " " + map.get("name") + 
						" " + map.get("rela") + " " + map.get("value"));
			}
		}
		PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
		ResultSet resultSet = preparedStatement.executeQuery();
		
		List<Account> accounts = new ArrayList<>();
		while (resultSet.next()) {
			Account account = new Account();
			account.setId(resultSet.getInt("id"));
			account.setAccount(resultSet.getString("account"));
			account.setAmount(resultSet.getDouble("amount"));
			account.setCreateDatetime(resultSet.getDate("create_datetime"));
			accounts.add(account);
		}
		return accounts;
	}
	
}
