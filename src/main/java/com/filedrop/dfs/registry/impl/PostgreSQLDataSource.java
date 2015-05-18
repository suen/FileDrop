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

public class PostgreSQLDataSource {

	public PostgreSQLDataSource() {

	}

	private Connection connectDB() {
		Connection c = null;
		try {
			// Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/filedrop_meta", "postgres",
					"postgres");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		//System.out.println("Opened database successfully");
		return c;
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