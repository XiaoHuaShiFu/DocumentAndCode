package db;

import java.sql.*;

public class DBUtil {
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/jdbc";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	private static Connection connection;
	
	static {
		try {
			//1.加载驱动程序
			Class.forName("com.mysql.jdbc.Driver");
			//2.获得数据库的连接
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}

	
}
