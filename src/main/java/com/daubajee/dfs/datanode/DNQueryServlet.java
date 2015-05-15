package com.daubajee.dfs.datanode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@SuppressWarnings("serial")
public class DNQueryServlet extends HttpServlet {

	@Override
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException {
		response.setContentType("text/plain");

		Map<String, String[]> paramMap = request.getParameterMap();
		
		if (paramMap.keySet().contains("list")){
			
			JSONObject reply = new JSONObject();
			
			File file = new File("./static/");
			String[] files = file.list();

			reply.put("result", files);
		
			
			response.getWriter().print(reply.toString());
			return;
		}
		
		if (paramMap.keySet().contains("size")){
			

			File directory = new File("./static/");
		   
			long length = 0;
		    for (File file : directory.listFiles()) {
		        if (file.isFile())
		            length += file.length();
		    }
			
			//long size = directory.getTotalSpace();
			JSONObject reply = new JSONObject();
			reply.put("result", String.valueOf(length));
			response.getWriter().print(reply.toString());
			return;
		}
		
		if (paramMap.keySet().contains("delete")){
			
			String[] filesToDelete = paramMap.get("delete");
			JSONObject reply = new JSONObject();
			
			Map<String, Boolean> result = new HashMap<String, Boolean>();
			for (String fn: filesToDelete){
				if (fn.isEmpty())
					continue;
				try {
					File file = new File("./static/"+fn);
					
					if(file.exists()){
						file.delete();
						result.put(fn, true);
					} else {
						result.put(fn, false);
					}
				} catch (Exception e) {
					result.put(fn, false);
					e.printStackTrace();
				}
			}
			reply.put("result", result);
			
			response.getWriter().print(reply.toString());
			return;
		}
		
		for(String param: paramMap.keySet()){
			for(String value: paramMap.get(param)){
				response.getWriter().println(param + " : " + value);
			}
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}