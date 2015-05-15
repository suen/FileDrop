package com.daubajee.dfs.namenode;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class NNQueryServlet extends HttpServlet {

	@Override
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException {
		response.setContentType("text/plain");

		Map<String, String[]> paramMap = request.getParameterMap();
		
		if (paramMap.keySet().contains("list")){
			
			String args = paramMap.get("list")[0];
			
			if (args==null){
				//TODO invalid request
			}

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

			//JSONObject reply = new JSONObject(mockstr);

			response.getWriter().print(mockstr);
			return;
		}
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}