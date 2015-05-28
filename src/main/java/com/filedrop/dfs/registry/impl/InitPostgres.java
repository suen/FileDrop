package com.filedrop.dfs.registry.impl;

import com.filedrop.dfs.namenode.NameNodeConfig;

public class InitPostgres {

	public static void main(String[] args) {
		NameNodeConfig.getInstance().init("./config/namenode.conf");
		PostgreSQLDataSource psql = new PostgreSQLDataSource();
		psql.initialisedb();
	}
	
}
