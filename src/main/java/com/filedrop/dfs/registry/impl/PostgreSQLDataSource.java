package com.filedrop.dfs.registry.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.filedrop.dfs.namenode.NameNodeConfig;

public class PostgreSQLDataSource {

	public PostgreSQLDataSource() {

	}

	private Connection connectDB() {
		Connection connection = null;
		try {
			NameNodeConfig config = NameNodeConfig.getInstance();
			String postgresHost = config.getValue("postgres_host");
			String postgresPort = config.getValue("postgres_port");
			String postgresUser = config.getValue("postgres_user");
			String postgresPwd = config.getValue("postgres_password");
			String postgresdb = config.getValue("postgres_database");
			
			connection = DriverManager.getConnection(
				"jdbc:postgresql://"+postgresHost+":"+postgresPort+"/"+postgresdb, 
				postgresUser,postgresPwd);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return connection;
	}

	public List<Map<String, String>> query(String sqlquery) {
		Connection c = connectDB();
		Statement statement = null;

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> row;
		try {
			statement = c.createStatement();

			ResultSet resultset = statement.executeQuery(sqlquery);

			ResultSetMetaData metaData = resultset.getMetaData();

			while (resultset.next()) {
				row = new HashMap<String, String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnName = metaData.getColumnName(i);
					
					String columnValue = resultset.getString(columnName);
					columnValue = columnValue==null ? "": columnValue;
					
					row.put(columnName, columnValue);
					// System.out.println(columnName +":"+
					// resultset.getString(columnName));
				}
				result.add(row);
			}
		} catch (Exception e) {
			return result;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public boolean insert(String query) {
		Connection c = connectDB();
		Statement statement = null;
		try {
			statement = c.createStatement();
			statement.executeUpdate(query);
			//System.err.println(query);
			//c.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

}