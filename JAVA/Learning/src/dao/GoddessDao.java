package dao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import db.DBUtil;
import model.Goddess;


public class GoddessDao {
	
	private static Connection connection;
	
	/**
	 * 初始化连接
	 */
	static {
		connection = DBUtil.getConnection();
	}
	
	public GoddessDao() {
	}
	
	/**
	 * 添加对象到数据库
	 * @param goddess
	 * @throws SQLException
	 */
	public void add(Goddess goddess) throws SQLException {
		String sql = "" + 
				" insert into goddess" + 
				" (user_name,sex,age,birthday,email,mobile," + 
				" create_user,create_date,update_user,update_date,isdel) " + 
				" values(" + 
				" ?,?,?,?,?,?,?,current_date(),?,current_date(),?)";
		//预编译,防止被注入
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		//传参
		preparedStatement.setString(1, goddess.getUser_name());
		preparedStatement.setInt(2, goddess.getSex());
		preparedStatement.setInt(3, goddess.getAge());
		preparedStatement.setDate(4, new Date(goddess.getBirthday().getTime()));
		preparedStatement.setString(5, goddess.getEmail());
		preparedStatement.setString(6, goddess.getMobile());
		preparedStatement.setString(7, goddess.getCreate_user());
//		preparedStatement.setDate(8, new Date(goddess.getBirthday().getTime()));
		preparedStatement.setString(8, goddess.getUpdate_user());
//		preparedStatement.setDate(10, new Date(goddess.getBirthday().getTime()));
		preparedStatement.setInt(9, goddess.getIsdel());
		//执行sql语句
		//execute执行新增、修改、删除数据库的操作
		preparedStatement.execute();
	}
	
	/**
	 * 通过对象更新数据库
	 * @param goddess
	 * @throws SQLException
	 */
	public void update(Goddess goddess) throws SQLException {
		String sql = "" + 
				" update goddess" + 
				" set user_name=?,sex=?,age=?,birthday=?,email=?,mobile=?," + 
				" update_user=?,update_date=current_date(),isdel=?" + 
				" where id=?";
		//预编译
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		//传参
		preparedStatement.setString(1, goddess.getUser_name());
		preparedStatement.setInt(2, goddess.getSex());
		preparedStatement.setInt(3, goddess.getAge());
		preparedStatement.setDate(4, new Date(goddess.getBirthday().getTime()));
		preparedStatement.setString(5, goddess.getEmail());
		preparedStatement.setString(6, goddess.getMobile());
		preparedStatement.setString(7, goddess.getUpdate_user());
		preparedStatement.setInt(8, goddess.getIsdel());
		preparedStatement.setInt(9, goddess.getId());
		//执行sql语句
		//execute执行新增、修改、删除数据库的操作
		preparedStatement.execute();
	}
	
	/**
	 * 通过id删除
	 * @param id
	 * @throws SQLException
	 */
	public void delete(Integer id) throws SQLException {
		String sql = "" + 
				" delete from goddess" + 
				" where id=?";
		//预编译
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		//传参
		preparedStatement.setInt(1, id);
		//执行sql语句
		preparedStatement.execute();
	}
	
	/**
	 * 查询所有
	 * @return
	 * @throws SQLException
	 */
	public List<Goddess> query() throws SQLException {
		//通过数据库的连接操作数据库，实现增删查改
		Statement statement = connection.createStatement();
		ResultSet resultSet =  statement.executeQuery("select * from goddess");
		
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
	
	/**
	 * 一个参数查询
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public List<Goddess> query(String name) throws SQLException {
		String sql = "" + 
				" select * from goddess" + 
				" where user_name=?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, name);
		ResultSet resultSet =  preparedStatement.executeQuery();
		
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
	
	/**
	 * 自定义个数参数查询
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public List<Goddess> query(List<Map<String, Object>> params) throws SQLException {
		StringBuilder sql = new StringBuilder("" + 
				" select * from goddess" + 
				" where ");
		if (params != null && params.size() > 0) {
			if (params.get(0).get("logic") == "and") {
				sql.append("1=1");
			} else if (params.get(0).get("logic") == "or") {
				sql.append("1=0");
			}
			for (Map<String, Object> map : params) {
				sql.append(" " + map.get("logic") + " " + map.get("name") + " " + map.get("rela") + " " + map.get("value"));
			}
		}
		PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
		ResultSet resultSet =  preparedStatement.executeQuery();
		
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
	
	/**
	 * 多个参数查询
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public List<Goddess> query(String name, String mobile) throws SQLException {
//		String sql = "" + 
//				" select * from goddess" + 
//				" where user_name=? and mobile=?";
//		PreparedStatement preparedStatement = connection.prepareStatement(sql);
//		preparedStatement.setString(1, name);
//		preparedStatement.setString(2, mobile);
		/**
		 * 模糊搜索
		 */
		String sql = "" + 
				" select * from goddess" + 
				" where user_name like ? and mobile like ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, "%" + name + "%");
		preparedStatement.setString(2, "%" + mobile + "%");
		
		
		ResultSet resultSet =  preparedStatement.executeQuery();
		
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
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Goddess get(Integer id) throws SQLException {
		String sql = "" + 
				" select * from goddess" + 
				" where id=?";
		//预编译
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		//传参
		preparedStatement.setInt(1, id);
		//执行sql语句
		//executeQuery()执行查询操作
		ResultSet resultSet = preparedStatement.executeQuery();
		Goddess goddess = null;
		while (resultSet.next()) {
			goddess = new Goddess();
			goddess.setId(id);
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
		}
		return goddess;
	}
	
}
