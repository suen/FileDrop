package com.filedrop.dfs.httpclient;

import org.eclipse.jetty.client.HttpClient;

public class DFSHttpClient {

	private HttpClient client;
	
	public final String listfile = "/query?list";
	public final String size = "/query?size";
	
	public DFSHttpClient(){
		client = new HttpClient();
	}

}
