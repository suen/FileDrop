package com.filedrop.dfs.registry.test;
import java.util.List;

import com.filedrop.dfs.registry.impl.SQLRegistry;
import com.filedrop.dfs.registry.model.DFile;
import com.filedrop.dfs.registry.model.Registry;

public class TestRegistry {

	static Registry registry = new SQLRegistry();
	
	public static void testFileDirCreation(){

		String id = registry.createUniqueFileName();
		System.out.println(id);
		
		registry.mkdir("/root");
		
		DFile entry = new DFile();
		entry.setId(id);
		entry.setType("file");
		entry.setParent("/root");
		entry.setName("myfile.txt");
		entry.setSize("0");
		
		registry.insertFile(entry);
		
		List<DFile> files = registry.list("/root");
		for(DFile file : files){
			System.out.println(file);
		}
		
		registry.rmfile("/root/myfile.txt");
		registry.rmdir("/root");
	}
	
	public static void testDNmapping(){
		String id = registry.createUniqueFileName();
//		registry.insertFile("/afile.big", id, "file");

		registry.createMappingDN(id, "datanode1");
		
		registry.rmmappingDN(id, "datanode1");
	}
	
	public static void insertTestData(){
		registry.mkdir("/textfiles");

		String id = registry.createUniqueFileName();
//		registry.insertFile("/textfiles/myfile.txt", id, "file");
		
		id = registry.createUniqueFileName();
//		registry.insertFile("/textfiles/fileText.txt", id, "file");

		id = registry.createUniqueFileName();
//		registry.insertFile("/textfiles/texts.txt", id, "file");
		
	}
	
	public static void main(String[] args) {
		
		TestRegistry.insertTestData();
		
		//TestRegistry.testFileDirCreation();
		//TestRegistry.testDNmapping();
			
	}

}
