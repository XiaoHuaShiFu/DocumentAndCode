package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.DBUtil;
import model.*;

public class TransInfoDao {
	private Connection connection;
	
	/**
	 * 初始化TransInfoDao对象
	 */
	public TransInfoDao() {
		super();
		this.connection = DBUtil.getConnection();
	}
	
	/**
	 * 添加日志
	 * @param transInfo
	 * @throws SQLException
	 */
	public void add(TransInfo transInfo) throws SQLException {
		String sql = "" + 
				" INSERT INTO trans_info" + 
				" (source_id, source_account, destination_id, destination_account, amount)" + 
				" VALUES(?, ?, ?, ?, ?);";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, transInfo.getSourceId());
		preparedStatement.setString(2, transInfo.getSourceAccount());
		preparedStatement.setInt(3, transInfo.getDestinationId());
		preparedStatement.setString(4, transInfo.getDestinationAccount());
		preparedStatement.setDouble(5, transInfo.getAmount());
		preparedStatement.execute();
	}
	
	/**
	 * 删除日志
	 * @param id
	 * @throws SQLException
	 */
	public void delete(Integer id) throws SQLException {
		String sql = "" + 
				" DELETE FROM trans_info" + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, id);
		preparedStatement.execute();
	}
	
	/**
	 * 更新日志
	 * @param transInfo
	 * @throws SQLException
	 */
	public void update(TransInfo transInfo) throws SQLException {
		String sql = "" + 
				" UPDATE trans_info" + 
				" SET source_id = ?, source_account = ?, destination_id = ?, destination_account = ?, amount = ? " + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, transInfo.getSourceId());
		preparedStatement.setString(2, transInfo.getSourceAccount());
		preparedStatement.setInt(3, transInfo.getDestinationId());
		preparedStatement.setString(4, transInfo.getDestinationAccount());
		preparedStatement.setDouble(5, transInfo.getAmount());
		preparedStatement.setInt(6, transInfo.getId());
		preparedStatement.execute();
	}
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public TransInfo get(Integer id) throws SQLException {
		String sql = "" + 
				" SELECT source_id, source_account, destination_id, destination_account, amount, create_datetime FROM trans_info" + 
				" WHERE id = ?;";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setDouble(1, id);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		TransInfo transInfo = new TransInfo();
		while (resultSet.next()) {
			transInfo.setId(id);
			transInfo.setSourceId(resultSet.getInt("source_id"));
			transInfo.setSourceAccount(resultSet.getString("source_account"));
			transInfo.setDestinationId(resultSet.getInt("destination_id"));
			transInfo.setDestinationAccount(resultSet.getString("destination_account"));
			transInfo.setAmount(resultSet.getDouble("amount"));
			transInfo.setCreateDatetime(resultSet.getDate("create_datetime"));
		}
		return transInfo;
	}
	
	/**
	 * 任意参数查询转账日志
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<TransInfo> query(List<Map<String, Object>> params) throws SQLException {
		StringBuilder sql = new StringBuilder("" + 
				" SELECT source_id, source_account, destination_id, destination_account, amount, create_datetime FROM trans_info" + 
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
		
		List<TransInfo> transInfos = new ArrayList<>();
		while (resultSet.next()) {
			TransInfo transInfo = new TransInfo();
			transInfo.setId(resultSet.getInt("id"));
			transInfo.setSourceId(resultSet.getInt("source_id"));
			transInfo.setSourceAccount(resultSet.getString("source_account"));
			transInfo.setDestinationId(resultSet.getInt("destination_id"));
			transInfo.setDestinationAccount(resultSet.getString("destination_account"));
			transInfo.setAmount(resultSet.getDouble("amount"));
			transInfo.setCreateDatetime(resultSet.getDate("create_datetime"));
			transInfos.add(transInfo);
		}
		return transInfos;
	}
}
