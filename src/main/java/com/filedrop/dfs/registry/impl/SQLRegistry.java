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
		
		String query = "select name, type, parent, id "
				+ "from registry "
				+ "where parent ='"+ level + "'";
		// System.out.println(query);
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
	public boolean createMapping(String path, String id, String type) {
		// TODO Auto-generated method stub
		DFile file = Utils.parseFilePath(path, type);
		return ds
				.insert("insert  into registry (name,type,parent,id) values ('"
						+ file.getName() + "','" + type + "','"
						+ file.getParent() + "','" + id + "')");
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
		
		DFile dir = Utils.parseFilePath(path, "dir");
		ds.insert("delete from registry where parent = '" + path
				+ "'");
		ds.insert("delete from registry where name like '" + dir.getName()
				+ "'");
		return true;
	}

	@Override
	public boolean rmfile(String path) {
		// TODO Auto-generated method stub
		DFile file = Utils.parseFilePath(path, "FILE");
		return ds.insert("delete from registry where parent = '"
				+ file.getParent() + "' and name = '" + file.getName() + "'");
	}

	@Override
	public boolean mvfile(String oldPath, String newPath) {
		// TODO Auto-generated method stub
		DFile oldfile = Utils.parseFilePath(oldPath, "FILE");
		DFile newfile = Utils.parseFilePath(newPath, "FILE");
		return ds.insert("update registry set parent='" + newfile.getParent()
				+ "' , name='" + newfile.getName() + "' where parent='"
				+ oldfile.getParent() + "' and name='" + oldfile.getName()
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
		return createMapping(path, "", "dir");
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

}
