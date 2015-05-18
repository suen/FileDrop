package com.filedrop.dfs.namenode;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class NNQueryServlet extends HttpServlet {

	
	private WebAppManager webManager;
	public NNQueryServlet() {
		webManager = new WebAppManager();
	}
	
	@Override
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException {
		response.setContentType("text/plain");

		Map<String, String[]> paramMap = request.getParameterMap();
		
		if (paramMap.keySet().contains("list")){
			String args = paramMap.get("list")[0];
			String resultStr = webManager.getDirectoryListing(args);
			
			response.getWriter().print(resultStr);
			return;
		}
		if (paramMap.keySet().contains("mkdir")){
			String args = paramMap.get("mkdir")[0];
			String resultStr = webManager.mkdir(args);
			
			response.getWriter().print(resultStr);
			return;
		}
		
		if (paramMap.keySet().contains("getfileurl")){
			String args = paramMap.get("getfileurl")[0];
			String resultStr = webManager.getFileUrl(args);
			
			response.getWriter().print(resultStr);
			return;
		}
		
		if (paramMap.keySet().contains("rm")){

			String filepath = paramMap.get("rm")[0];
			
			if (paramMap.keySet().contains("id")){
				String fileid = paramMap.get("id")[0];
				String resultStr = webManager.rmFile(filepath, fileid);
				response.getWriter().print(resultStr);
			}
			
			else {
				String resultStr = webManager.rmDir(filepath);
				response.getWriter().print(resultStr);
			}
			
			return;
		}
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}