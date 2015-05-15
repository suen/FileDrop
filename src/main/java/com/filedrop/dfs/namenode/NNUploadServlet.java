package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.server.Authentication.SendSuccess;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class NNUploadServlet extends HttpServlet
{

	private boolean isMultipart;
	private String filePath;

	private File file ;

	@Override
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException {
		response.setContentType("text/html");
		response.getWriter().println("<h1>Hello world</h1>");
	}

	protected void doPosts(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		double intial = System.currentTimeMillis();

		FileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
		try {
			List fileItems = servletFileUpload.parseRequest(request);
			Iterator iterator = fileItems.iterator();

			System.out.println("finished: " + (System.currentTimeMillis()-intial));
			while (iterator.hasNext()) {
				FileItem fileItem = (FileItem) iterator.next();
				boolean formFied = fileItem.isFormField();
				if (formFied) {
					System.out.println("regular form field: " + fileItem.getFieldName());
				} else {
					String fileName = "./tmp/"+ fileItem.getName();
					OutputStream outputStream = new FileOutputStream(fileName);
					InputStream inputStream = fileItem.getInputStream();

					int readBytes = 0;
					byte[] buffer = new byte[10000];
					while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
						outputStream.write(buffer, 0, readBytes);
					}
					outputStream.close();
					inputStream.close();
					double fin = System.currentTimeMillis() - intial;
					System.out.println("Finished: "+ fin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printallparam(HttpServletRequest request){
		Map<String, String[]> paramMap = request.getParameterMap();
		
		for(String key: paramMap.keySet()){
			for(String value: paramMap.get(key))
				System.out.println(key + ": " + value);
		}

	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		printallparam(request);
		
		response.setContentType("text/plain");
		JSONObject reply = new JSONObject();
		
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if( !isMultipart ){
			reply.put("result", "failed");
			response.getWriter().print(reply.toString());
			System.out.println(reply.toString());
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(new File("./tmp"));
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		
		String fileName = "";
		String cwd = "";
		try{ 
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);
			// Process the uploaded file items
			Iterator i = fileItems.iterator();
			while (i.hasNext ()) {
				FileItem fi = (FileItem) i.next();
				if ( !fi.isFormField () )	{
					fileName = fi.getName();
					// Write the file
					if( fileName.lastIndexOf("\\") >= 0 ){
						file = new File( "./tmp/" + 
								fileName.substring( fileName.lastIndexOf("\\"))) ;
						fi.write( file ) ;

					}else {
						file = new File( "./tmp/" + 
								fileName.substring(fileName.lastIndexOf("\\")+1)) ;
						fi.write( file ) ;
					}
				} else {
					String fieldname = fi.getFieldName();

					if (fieldname.equals("cwd")){
						cwd = fi.getString();
					}
	
			}
			}
		} catch(Exception ex) {
			reply.put("result", "failed");
			ex.printStackTrace();
			System.out.println(reply.toString());
		}
		

		if (cwd.isEmpty()){
			reply.put("result", "failed");
			reply.put("error", "Current working directory missing");
			response.getWriter().print(reply.toString());
			System.out.println(reply.toString());
			return;
		} else {
			//System.out.println("cwd: " + reqcloned.getParameter("cwd"));
			
			Map<String, String> result = new HashMap<String, String>(); 
			result.put(cwd + "/" + fileName, "OK");
			reply.put("result", result);
			response.getWriter().print(reply.toString());
			System.out.println(reply.toString());
		}


	}
}