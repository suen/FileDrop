package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NameNodeConfig {
	
	private static NameNodeConfig self = null;
	private String configFilePath = "";
	
	public static NameNodeConfig getInstance(){
		if (self==null)
			self = new NameNodeConfig();
		return self;
	}
	
	private Map<String, String> configs = new HashMap<String, String>();

	private NameNodeConfig(){
		
	}
	
	public void init(){
		if (configFilePath.isEmpty())
			return;
		init(configFilePath);
	}
	
	public void init(String configFilepath){
		
		this.configFilePath = configFilepath;
		
		File configfile = new File(configFilepath);
		if (!configfile.exists()){
			System.err.println("FATAL: Config file  "
					+ "'"+configFilepath+"' does not exists");
			return;
		}

		try {
			Properties prop = new Properties();
			InputStream iStream = new FileInputStream(configfile);
			prop.load(iStream);

			for(Object key: prop.keySet()) {
				configs.put(key.toString(), prop.get(key).toString());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getValue(String key){
		return configs.get(key);
	}
	
	
}
