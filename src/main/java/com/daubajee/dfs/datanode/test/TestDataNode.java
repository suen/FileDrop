package com.daubajee.dfs.datanode.test;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;


public class TestDataNode {


//	public static void mains(String[] args) throws Exception {
//	    HttpClient httpclient = new DefaultHttpClient();
//	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//
//	    HttpPost httppost = new HttpPost("http://localhost:8080/upload");
//	    File file = new File("./testdata/serie.mp4");
//
//	    MultipartEntity mpEntity = new MultipartEntity();
//	    ContentBody cbFile = new FileBody(file, "application/octet-stream");
//	    mpEntity.addPart("file", cbFile);
//
//
//	    httppost.setEntity(mpEntity);
//	    System.out.println("executing request " + httppost.getRequestLine());
//	    HttpResponse response = httpclient.execute(httppost);
//	    HttpEntity resEntity = response.getEntity();
//
//	    System.out.println(response.getStatusLine());
//	    if (resEntity != null) {
//	      System.out.println(EntityUtils.toString(resEntity));
//	    }
//	    if (resEntity != null) {
//	      resEntity.consumeContent();
//	    }
//	    
//
//	    httpclient.getConnectionManager().shutdown();
//	  }

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		// Configure HttpClient here
		httpClient.start();
		
		
        Response response = httpClient.newRequest("localhost", 8080)
        		.file(Paths.get("./testdata/serie.mp4")).send();
		
//		ContentResponse response = httpClient
//		        .GET("http://localhost:8080/query?list");
//		
		System.out.println(response.getStatus());

		httpClient.stop();
	}
	
}
