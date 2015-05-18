package com.filedrop.dfs.datanode;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class DataNodeServer {
	
	public DataNodeServer(){
		
	}

	public static void main(String[] args) throws Exception {
		int port = 8000;
		if (args.length==1){
			port = Integer.valueOf(args[0]);
		}
		
		
		final ServletContextHandler context =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase("static");
		context.setWelcomeFiles(new String[]{"index.html"});

		context.setClassLoader(
				Thread.currentThread().getContextClassLoader()
				);

		context.addServlet(DefaultServlet.class, "/");

		ServletHolder fileUploadServletHolder = new ServletHolder(new DNUploadServlet());
		fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
		//	        context.addServlet(fileUploadServletHolder, "/fileUpload");

		context.addServlet(fileUploadServletHolder, "/upload");

		context.addServlet(DNQueryServlet.class, "/query");

		System.out.println("Initializing datenode server on port  " +port + " .....");
		
		final Server server = new Server(port);
		server.setHandler(context);

		System.out.println("Ready");

		server.start();
		server.join();

	}


}
