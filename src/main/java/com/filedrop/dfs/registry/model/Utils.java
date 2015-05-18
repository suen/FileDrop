package com.filedrop.dfs.registry.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Utils {

	public static String removeSlash(String path){
		
		path = path.replace("//", "/");
		
		if (path.length()==1)
			return path;
		
		if (path.charAt(path.length()-1) == '/') {
			path = path.substring(0, path.length()-1);
		}
		return path;
	}
	
	public static DFile parseFilePath(String myFilePath, String type){
		
		myFilePath = removeSlash(myFilePath);
		
		String delims = "[/]";
		String[] tokens = myFilePath.split(delims);
		
		String path = "";
		
		for(int i = 0;i<tokens.length-1;i++){
			path = path + tokens[i] + "/";
		}
		
		path = removeSlash(path);
		
		String fileName = tokens[tokens.length-1];
		
		//System.out.println("PATH : " + path);
		//System.out.println("FILENAME : " + fileName);
		
		DFile myFile = new DFile(fileName,type,path);
		return myFile;
	}
	
	public static  List<DFile> getFileList(List<Map<String, String>> result){
		
        List<DFile> myResults = new ArrayList<DFile>();
        DFile myFile = null;
        
        for(Map<String, String> tuple: result){
        	//System.out.println(tuple.get("name"));
        	myFile = new DFile(tuple.get("name"),tuple.get("type"),
        			tuple.get("parent"));
        	myFile.setId(tuple.get("id"));
        	//System.out.println(myFile.getName());
        	myResults.add(myFile);
        }
        //System.out.println("taille myResults "+myResults.size());
		return myResults;
	}
	
	
	
}
