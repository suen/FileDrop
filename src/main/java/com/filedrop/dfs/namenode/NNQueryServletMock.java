package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@SuppressWarnings("serial")
public class NNQueryServletMock extends HttpServlet {

	@Override
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException {
		response.setContentType("text/plain");

		Map<String, String[]> paramMap = request.getParameterMap();
		
		if (paramMap.keySet().contains("list")){
			
			String args = paramMap.get("list")[0];
			
			String root = "./tmp";
			String path = "";
			if (args!=null){
				path = path + args;
			}
			
			System.out.println("Path is : " + path);

			String mockstr = "{\"path\": \"root\","
					+ "\"filelist\": ["
					+ "{\"size\": 0, \"name\": \"archive\", \"type\": \"dir\"}, "
					+ "{\"size\": 543, \"name\": \"file01.txt\", \"type\": \"file\"}, "
					+ "{\"size\": 91, \"name\": \"file02.txt\", \"type\": \"file\"}, "
					+ "{\"size\": 412, \"name\": \"file03.txt\", \"type\": \"file\"}, "
					+ "{\"size\": 275, \"name\": \"file04.txt\", \"type\": \"file\"}, "
					+ "{\"size\": 130, \"name\": \"file10.txt\", \"type\": \"file\"}, "
					+ "{\"size\": 1499, \"name\": \"file11.txt\", \"type\": \"file\"}"
					+ "]}";

			File tmpDir = new File(root + path);
			
			String[] files = tmpDir.list();
			JSONObject reply = new JSONObject();
			
			Map<String, String> finfo;
			List<Map<String, String>> filelist = new ArrayList<Map<String,String>>();
			File aFile;
			for(String fn: files){
				finfo = new HashMap<String, String>();
				
				String filepath = (path +"/"+ fn).replace("//", "/");
				aFile = new File(root + filepath);
				
				finfo.put("name", fn);
				finfo.put("size", String.valueOf(aFile.length()));
				finfo.put("type", (aFile.isFile()?"file": "dir"));
				finfo.put("path", (path+"/"+fn).replace("//", "/"));
				filelist.add(finfo);
			}
			
			reply.put("filelist", filelist);
			reply.put("path", args);

			response.getWriter().print(reply.toString());
			return;
		}
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}