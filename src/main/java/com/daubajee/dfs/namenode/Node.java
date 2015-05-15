package com.daubajee.dfs.namenode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class Node {

	private String ip;
	private String port;
	private String name;
	private long totalspace;
	private HttpClient client;

	public final String listfile = "/query?list";
	public final String size = "/query?size";


	public String getIdentifier(){
		return name +"@" + ip +  ":" + port;
	}

	public String getURL(){
		return "http://" + ip + ":" + port;
	}

	public Node(String ip, String name, String port, long totalspace){
		this.ip = ip;
		this.name = name;
		this.port = port;
		this.totalspace = totalspace;
		client = new HttpClient();
	}

	public String getPort(){
		return port;
	}

	public void setPort(String port){
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTotalSpace(){
		return totalspace;
	}
	
	public void setTotalSpace(long totalspace){
		this.totalspace = totalspace;
	}
	
	public long getOccupiedSpace(){
		try {

			client.start();
			ContentResponse response = client.GET(getURL()+size);

			JSONObject replyJson = new JSONObject(response.getContentAsString());

			long size = 1;
			size = Long.parseLong(replyJson.getString("result"));

			return (size==0 ? 1 : size);

		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public List<String> getFileList() {
		
		try {
			List<String> list = new ArrayList<String>();
			
			client.start();
			ContentResponse response = client.GET(getURL()+listfile);
			
			JSONObject replyJson = new JSONObject(response.getContentAsString());
			
			JSONArray jsonArray = replyJson.getJSONArray("result");
			
			for(int i=0; i<jsonArray.length(); i++){
				list.add(jsonArray.getString(i));
			}
			return list;
			
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}
}
