package com.filedrop.dfs.registry.model;

import java.util.List;

public interface Registry {
	
	List<DFile> list(String level);
	
	String createUniqueFileName();
	
	boolean insertFile(DFile file);
	
	boolean createMappingDN(String id,String datanode);
	
	boolean mkdir(String path);
	
	boolean rmdir(String path);
	
	boolean rmfile(String path);
	
	boolean mvfile(String oldPath, String newPath);
	
	boolean rmmappingDN(String id,String datanode);
	
	String[] getDatanodeForFileID(String id);

	List<DFile> getTree();
	
	
}
