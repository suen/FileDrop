package com.filedrop.dfs.namenode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
	public final String delete = "/query?delete";
	public final String upload = "/upload";
	public final String download = "/query?download";


	public String getIdentifier(){
		return name +"@" + ip +  ":" + port;
	}

	public String getURL(){
		return ("http://" + ip + ":" + port).trim();
	}

	public Node(String ip, String name, String port, long totalspace){
		this.ip = ip;
		this.name = name;
		this.port = port.trim();
		this.totalspace = totalspace;
		client = new HttpClient();
	}

	public String getPort(){
		return port;
	}

	public void setPort(String port){
		this.port = port.trim();
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
	
	public void deleteFile(String id){
		String delreq = getURL()+delete+"="+id;
		try {

			client.start();
			ContentResponse response = client.GET(delreq);

			try {
				JSONObject replyJson = new JSONObject(response.getContentAsString());
				JSONObject result = replyJson.getJSONObject("result");
				System.out.println(result.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  catch (Exception e) {
			String classname = e.getStackTrace()[0].getClassName();
			System.out.println("Delete request failed: " + delreq);
			System.out.println(classname);
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean exists(String filename) throws Exception{
		client.start();
		String request = getURL()+"/query?exists="+filename;
		ContentResponse response = client.GET(request);
		
		client.stop();
		System.out.println("Reply for '" + request + "': " + response.getContentAsString());
		JSONObject replyJson = new JSONObject(response.getContentAsString());
		return replyJson.getBoolean("result");
	}

	
	public String uploadFile(String filepath){
		PyHttpClient pyclient = new PyHttpClient();
		return pyclient.upload(getURL()+upload, filepath);
	}
	
	
	public void downloadFile(String url, String filename){
		try {

			client.start();
			ContentResponse response = 
					client.GET(getURL()+download+"="+url+"&filename="+filename);

			try {
				JSONObject replyJson = new JSONObject(response.getContentAsString());
				JSONObject result = replyJson.getJSONObject("result");
				System.out.println(result.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class PyHttpClient {

	public String upload(String url, String filepath){
	
    try {
		Process process = Runtime.getRuntime().exec(
				"python clientpy.py " + url + " " + filepath);
		//InputStream stdout = process.getInputStream();
		
		InputStream stderr = process.getErrorStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
		
		String output = "";
		String line;
		while((line = br.readLine())!=null){
			output += line + "\n";
		}
		
		//System.out.println(output);
		
		return output.trim();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
      
      return "";
	}
	
	public static void main(String[] args) {
		PyHttpClient py = new PyHttpClient();
		
		py.upload("http://localhost:8080/upload", "/d/ent/movies/A.Walk.Among.The.Tombstones.2014.FANSUB.VOSTFR.HDRiP.CROPPED.XviD.AC3-NIKOo-ZT.avi");
	}
	
}
