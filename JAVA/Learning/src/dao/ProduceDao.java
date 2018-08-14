package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBUtil;
import model.Goddess;

public class ProduceDao {
	
	public static int select_count() throws SQLException {
		Connection connection = DBUtil.getConnection();
		CallableStatement callableStatement = connection.prepareCall("CALL sp_select_count(?)");
		callableStatement.registerOutParameter(1,Types.INTEGER);
		callableStatement.execute();
		//获得出参
		int count = callableStatement.getInt(1);
		return count;
	}
	
	public static List<Goddess> select_filter(String sp_name) throws SQLException {
		Connection connection = DBUtil.getConnection();
		CallableStatement callableStatement = connection.prepareCall("CALL sp_select_filter(?)");
		callableStatement.setString(1, sp_name);
		callableStatement.execute();
		ResultSet resultSet = callableStatement.getResultSet();
		
		List<Goddess> goddesses = new ArrayList<>();
		while (resultSet.next()) {
			Goddess goddess = new Goddess();
			goddess.setId(resultSet.getInt("id"));
			goddess.setUser_name(resultSet.getString("user_name"));
			goddess.setSex(resultSet.getInt("sex"));
			goddess.setAge(resultSet.getInt("age"));
			goddess.setBirthday(resultSet.getDate("birthday"));
			goddess.setEmail(resultSet.getString("email"));
			goddess.setMobile(resultSet.getString("mobile"));
			goddess.setCreate_user(resultSet.getString("create_user"));
			goddess.setUpdate_user(resultSet.getString("update_user"));
			goddess.setCreate_date(resultSet.getDate("create_date"));
			goddess.setUpdate_date(resultSet.getDate("update_date"));
			goddess.setIsdel(resultSet.getInt("isdel"));
			goddesses.add(goddess);
		}
		return goddesses;
	}
	
	
	public static List<Goddess> select_nofilter() throws SQLException {
		//1.获得连接
		Connection connection = DBUtil.getConnection();
		//2.获得CallableStatement
		CallableStatement callableStatement = connection.prepareCall("CALL sp_select_nofilter()");
		//3.则行存储过程
		callableStatement.execute();
		//4.处理返回结果。结果集，出参
		ResultSet resultSet = callableStatement.getResultSet();
		
		List<Goddess> goddesses = new ArrayList<>();
		while (resultSet.next()) {
			Goddess goddess = new Goddess();
			goddess.setId(resultSet.getInt("id"));
			goddess.setUser_name(resultSet.getString("user_name"));
			goddess.setSex(resultSet.getInt("sex"));
			goddess.setAge(resultSet.getInt("age"));
			goddess.setBirthday(resultSet.getDate("birthday"));
			goddess.setEmail(resultSet.getString("email"));
			goddess.setMobile(resultSet.getString("mobile"));
			goddess.setCreate_user(resultSet.getString("create_user"));
			goddess.setUpdate_user(resultSet.getString("update_user"));
			goddess.setCreate_date(resultSet.getDate("create_date"));
			goddess.setUpdate_date(resultSet.getDate("update_date"));
			goddess.setIsdel(resultSet.getInt("isdel"));
			goddesses.add(goddess);
		}
		return goddesses;
	}
	
	
}
