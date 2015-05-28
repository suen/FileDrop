package com.filedrop.dfs.registry.impl;

import java.sql.DriverManager;

import com.filedrop.dfs.namenode.NameNodeConfig;

public class InitPostgres {

	public static void main(String[] args) {
		try {
			NameNodeConfig.getInstance().init("./config/namenode.conf");
			PostgreSQLDataSource psql = new PostgreSQLDataSource();
			psql.initialisedb();
		} catch (Exception e) {
			e.printStackTrace();
			
			NameNodeConfig config = NameNodeConfig.getInstance();
			String postgresHost = config.getValue("postgres_host");
			String postgresPort = config.getValue("postgres_port");
			String postgresUser = config.getValue("postgres_user");
			String postgresPwd = config.getValue("postgres_password");
			String postgresdb = config.getValue("postgres_database");
			
			String url = "jdbc:postgresql://"+postgresHost+":"+postgresPort+"/"+postgresdb;
			System.out.println("Failed : " + url + postgresUser + ":" +postgresPwd);
			
		}
	}
	
}
