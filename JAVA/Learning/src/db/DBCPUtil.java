package db;

import java.sql.*;
import java.util.Properties;

import javax.sql.*;

import org.apache.commons.dbcp2.*;

public class DBCPUtil {
	
	//数据源
	private static DataSource DS;
	
	private static final String configFile = "/dbcp.properties";
	
	static {
		Properties prop = new Properties();
		try {
			prop.load(Object.class.getResourceAsStream(configFile));
			DS = BasicDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * 通过配置文件配置连接池变量
//	 */
//	private static void initDbcp() {
//		Properties prop = new Properties();
//		try {
//			prop.load(Object.class.getResourceAsStream(configFile));
//			DS = BasicDataSourceFactory.createDataSource(prop);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
	/**
	 * 手动配置文件配置连接池变量
	 */
	public static void initDS(String connectURL, String userName, String password, String driverClassName, int initialSize, 
			int maxTotal, int maxIdle, int maxWaitMillis, int minIdle) {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(driverClassName);
		basicDataSource.setUsername(userName);
		basicDataSource.setPassword(password);
		basicDataSource.setUrl(connectURL);
		basicDataSource.setInitialSize(initialSize);
		basicDataSource.setMaxTotal(maxTotal);
		basicDataSource.setMaxIdle(maxIdle);
		basicDataSource.setMaxWaitMillis(maxWaitMillis);
		basicDataSource.setMinIdle(minIdle);
		DS = basicDataSource;
	}
	
	//获取数据库连接的公用方法
	public static Connection getConnection() {
		Connection connection = null;
		if (DS != null) {
			try {
				connection = DS.getConnection();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			
			try {
				connection.setAutoCommit(false);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			return connection;
		}
		return connection;
	}
	
}
