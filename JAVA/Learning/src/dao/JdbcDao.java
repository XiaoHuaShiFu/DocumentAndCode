package dao;

import java.sql.*;
import java.util.Arrays;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import db.DBMysql;

public class JdbcDao {
	
	public static void main(String[] args) throws SQLException {
//		runTest();
//		preparedRunTest();
//		insertRunTest();
//		scrollRunTest();
//		uodateRow();
		
//		insertRow();
//		deleteRow();
//		deleteRowSet();
//		metaData();
//		insertTrans();
		batchUpdate();
	}	
	
	public static void runTest() throws SQLException {
		try (Connection conn = DBMysql.getConnection(); Statement stat = conn.createStatement()) {
			//executeUpdate返回受影响的行数
			int rows = stat.executeUpdate("INSERT INTO run_test VALUES('Hello1, World2!'),('Hello2, World1!');");
			System.out.println("rows=" + rows);
			stat.executeQuery("SELECT * FROM run_test;");
			stat.executeQuery("SELECT * FROM payment;");
			try (ResultSet result = stat.getResultSet()) {
				int counts = stat.getUpdateCount();
				System.out.println(counts + "|" + result.findColumn("staff_id"));
				while (result.next()) {
					System.out.println(result.getString(1) + "id=" + result.getString(2));
				}
				//异常
				SQLWarning w = stat.getWarnings();
				while (w != null) {
					w.printStackTrace();
					w = w.getNextWarning();
				}
			}
		}
	}

	public static void preparedRunTest() throws SQLException {
		String sql = "" + 
				" SELECT books.Price, books.Title " + 
				" FROM books,publishers " + 
				" WHERE books.Publisher_Id = publishers.Publisher_Id" + 
				" AND publishers.Name = ?";
		try (Connection conn = DBMysql.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
			stat.setString(1, "bcbs");
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1) + "|" + rs.getString(2));
			}
		}
	}
	
	public static void insertRunTest() throws SQLException {
		String sql = "" + 
				" INSERT INTO books (Price, Title, Publisher_Id)" + 
				" VALUES(1000, 'g' , '3'),(2000, 'h' , 3)";
		try (Connection conn = DBMysql.getConnection(); Statement stat = conn.createStatement()) {
			//获取自动生成的键
			stat.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stat.getGeneratedKeys();
			
			while (rs.next()) {
				System.out.println(rs.getInt(1));
			}
		}
	}
	
	public static void scrollRunTest() throws SQLException {
		String sql = "" + 
				" SELECT * FROM books";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			ResultSet rs = stat.executeQuery(sql);
			if (rs.relative(1)) {
				System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + "|" + rs.getString(4));
			}
			if (rs.relative(1)) {
				System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + "|" + rs.getString(4));
			}
			rs.absolute(8);
			System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + "|" + rs.getString(4));
			System.out.println(rs.getRow());
			if (rs.previous()) {
				System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + "|" + rs.getString(4));
			}
		}
	}
	
	public static void uodateRow() throws SQLException {
		String sql = "" + 
				" SELECT * FROM books";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("Id") == 6) {
					double increase = 10000;
					double price = rs.getDouble("Price");
					//更新行
					rs.updateDouble("Price", price + increase);
					//取消对当前行的更新
					rs.cancelRowUpdates();
					//更新数据库
					rs.updateRow();
				}
			}
		}
	}
	
	public static void insertRow() throws SQLException {
		String sql = "" + 
				" SELECT * FROM books";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = stat.executeQuery(sql);
			rs.moveToInsertRow();
			rs.updateDouble("Price", 998);
			rs.updateString("Title", "insertBook");
			rs.updateInt("Publisher_Id", 3);
			rs.insertRow();
			rs.moveToCurrentRow();
		}
	}
	
	public static void deleteRow() throws SQLException {
		String sql = "" + 
				" SELECT * FROM books";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("Id") == 7) {
					rs.deleteRow();
				}
			}
		}
	}
	
	/**
	 * 带缓冲的行集
	 * @throws SQLException
	 */
	public static void deleteRowSet() throws SQLException {
		String sql = "" + 
				" SELECT * FROM books";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = stat.executeQuery(sql);
			RowSetFactory factory = RowSetProvider.newFactory();
			CachedRowSet crs = factory.createCachedRowSet();
			crs.populate(rs);
			conn.close();
			crs.setUrl("jdbc:mysql://127.0.0.1:3306/len_mysql");
			crs.setUsername("root");
			crs.setPassword("root");
			crs.setCommand(sql);
			while (crs.next()) {
				System.out.println(crs.getString(1) + "|" + crs.getString(2) + "|" + crs.getString(3) + "|" + crs.getString(4));
				if (crs.getInt("Id") == 8) {
					crs.deleteRow();
					crs.acceptChanges(conn);
				}
			}
		}
	}
	
	public static void metaData() throws SQLException {
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			DatabaseMetaData meta = conn.getMetaData();
			String[] types = {};
			ResultSet rs = meta.getTables("", "", "books", types);
			while (rs.next()) {
				System.out.println(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + 
						 "|" + rs.getString(4) + "|" + rs.getString(5));
			}
		}
	}
	
	public static void insertTrans() throws SQLException {
		String sql = "" + 
				" INSERT INTO books (Price, Title, Publisher_Id) " + 
				" VALUES(987,'TransTest',3)";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement()) {
			conn.setAutoCommit(false);
			stat.executeUpdate(sql);
			stat.executeUpdate(sql);
			stat.executeUpdate(sql);
			stat.executeUpdate(sql);
			conn.rollback();
			conn.commit();
		}
	}
	
	public static void batchUpdate() throws SQLException {
		String sql = "" + 
				" INSERT INTO books (Price, Title, Publisher_Id) " + 
				" VALUES(987,'BatchUpdate',3)";
		try (Connection conn = DBMysql.getConnection(); 
				Statement stat = conn.createStatement()) {
			conn.setAutoCommit(false);
			stat.addBatch(sql);
			stat.addBatch(sql);
			stat.addBatch(sql);
			stat.addBatch(sql);
			int[] counts = stat.executeBatch();
			System.out.println(Arrays.toString(counts));
			conn.commit();
			conn.setAutoCommit(true);
		}
	}
	
}
