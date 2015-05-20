package com.filedrop.dfs.registry.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import com.filedrop.dfs.registry.model.DFile;
import com.filedrop.dfs.registry.model.Registry;
import com.filedrop.dfs.registry.model.Utils;

public class SQLRegistry implements Registry {
	PostgreSQLDataSource ds;

	public SQLRegistry() {
		ds = new PostgreSQLDataSource();
	}

	@Override
	public List<DFile> list(String level) {

		level = Utils.removeSlash(level);
		
		String query = "select name, type, parent, size, id "
				+ "from registry "
				+ "where parent ='"+ level + "'";
		// System.out.println(query);
		List<Map<String, String>> result = ds.query(query);
		return Utils.getFileList(result);
	}
	
	@Override
	public List<DFile> getTree() {

		String query = "select name, type, parent, size, id "
				+ "from registry "
				+ "where lower(type) = 'file' ";

		List<Map<String, String>> result = ds.query(query);
		return Utils.getFileList(result);
	}

	@Override
	public String createUniqueFileName() {
		// TODO Auto-generated method stub
		BigInteger bInt = new BigInteger(64, new Random());
		//System.out.println(bInt.toString());
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return DatatypeConverter.printHexBinary(md.digest(bInt.toString()
					.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public boolean insertFile(DFile file) {
		return ds.insert("insert into registry "
				+ "(name,type,parent, size,id) "
				+ "values ("
						+ "'" +file.getName() + "','" + file.getType() + "',"
						+ "'" +file.getParent() + "','" + file.getSize()+ "',"
						+ "'" +file.getId()+ "'"
						+ ")");
	}

	@Override
	public boolean createMappingDN(String id, String datanode) {
		// TODO Auto-generated method stub
		return ds.insert("insert  into location (id,datanode) values ('" + id
				+ "','" + datanode + "')");
	}

	// @Override
	@Override
	public boolean rmdir(String path) {
		
		path = Utils.removeSlash(path);
		
		String query = "select * from registry where parent = '" + path + "'";

		List<Map<String, String>> result = ds.query(query);

		if (result.size() > 0){
			return false;
		}
		
		String[] filepath = Utils.parseFilePath(path);
		ds.insert("delete from registry where parent = '" + path
				+ "'");
		ds.insert("delete from registry where name like '" + filepath[0]
				+ "'");
		return true;
	}

	@Override
	public boolean rmfile(String path) {
		// TODO Auto-generated method stub
		String[] filepath = Utils.parseFilePath(path);
		return ds.insert("delete from registry where parent = '"
				+ filepath[1] + "' and name = '" + filepath[0] + "'");
	}

	@Override
	public boolean mvfile(String oldPath, String newPath) {
		// TODO Auto-generated method stub
		String[] ofilepath = Utils.parseFilePath(oldPath);
		String[] nfilepath = Utils.parseFilePath(newPath);
		return ds.insert("update registry "
				+ "set parent='" + nfilepath[1] + "',"
						+ "name='" + nfilepath[0] + "' where parent='"
				+ ofilepath[1] + "' and name='" + ofilepath[0]
				+ "' ");
	}

	@Override
	public boolean rmmappingDN(String id, String datanode) {
		// TODO Auto-generated method stub

		return ds.insert("delete from location where id = '" + id
				+ "' and datanode = '" + datanode + "'");
	}

	@Override
	public boolean mkdir(String path) {
		
		String[] dirpath = Utils.parseFilePath(path);
		DFile dir = new DFile();
		dir.setId("");
		dir.setName(dirpath[0]);
		dir.setParent(dirpath[1]);
		dir.setType("dir");
		dir.setSize("");
		
		return insertFile(dir);
	}

	@Override
	public String[] getDatanodeForFileID(String id) {
		String query = "select datanode, id"
				+ " from location "
				+ " where id ='"+ id + "'";

		List<Map<String, String>> result = ds.query(query);
		
		if (result.size()==0)
			return null;
		
		String[] dnames = new String[result.size()];
		
		int i=0;
		for(Map<String, String> res: result){
			dnames[i++] = res.get("datanode");
		}
		
		return dnames;
	}
	
	public static void createdatabaseSchema(){
		String create = "create table registry(name text, type text, parent text, id text, size text);";
	}

}
