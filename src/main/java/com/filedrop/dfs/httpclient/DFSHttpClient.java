package com.filedrop.dfs.httpclient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

public class DFSHttpClient {

	private HttpClient client;
	
	public final String listfile = "/query?list";
	public final String size = "/query?size";
	
	public static void testClient() throws Exception{
		HttpClient client = new HttpClient();
		client.start();
		
		ContentResponse resp = client.GET("http://localhost:8009/8D055C37E8149AC4909F123243B92F8E702E9DD3DC33B52569F1AD8B941C1700");
		
		Path path = Paths.get("afile");
		
		try {
			Files.write(path, resp.getContent()); // Requires System.IO
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.stop();
		
	}
	
	public static void main(String[] args) throws Exception {
		testClient();
	}

}
