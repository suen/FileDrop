package com.daubajee.dfs.datanode;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class DataNodeServer {
	
	public DataNodeServer(){
		
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Initializing server...");
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

		
		final Server server = new Server(8000);
		server.setHandler(context);

		server.start();
		server.join();

	}


}
