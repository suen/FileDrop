package com.filedrop.dfs.namenode;

import java.net.InetSocketAddress;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class NameNodeHTTPServer {

	private NameNodeConfig config;
	
	public NameNodeHTTPServer() {

	}
	
	public void configInit(){
		config = NameNodeConfig.getInstance();
		config.init("./config/namenode.conf");
		WebAppManager.getInstance();
		
	}

	public void serverInit() throws Exception {
		
		final ServletContextHandler context =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.setResourceBase("web");
		context.setWelcomeFiles(new String[]{"index.html"});

		context.setClassLoader(
				Thread.currentThread().getContextClassLoader()
				);

		context.addServlet(DefaultServlet.class, "/");

		ServletHolder fileUploadServletHolder = new ServletHolder(new NNUploadServlet());
		fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));

		context.addServlet(NNUploadServlet.class, "/upload");

		context.addServlet(NNQueryServlet.class, "/query");
		
		System.out.println("Initializing namenode server...");
		
		String hostname = (config.getValue("namenode_host") == null ? 
					"localhost" : config.getValue("namenode_host"));
		String port = (config.getValue("namenode_host") == null ? 
				"8080" : config.getValue("namenode_host"));

		InetSocketAddress sock = new InetSocketAddress(hostname, Integer.valueOf(port));
		final Server server = new Server(sock);

		server.setHandler(context);

		server.start();
		System.out.println("Hostname="+hostname + " Port="+port);

		server.join();

	}
	
	public static void main(String[] args) throws Exception {
		NameNodeHTTPServer nnserver = new NameNodeHTTPServer();
		nnserver.configInit();
		nnserver.serverInit();
	}
	
}
