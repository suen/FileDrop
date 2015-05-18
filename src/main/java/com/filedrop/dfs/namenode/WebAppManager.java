package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.filedrop.dfs.registry.impl.SQLRegistry;
import com.filedrop.dfs.registry.model.DFile;
import com.filedrop.dfs.registry.model.Registry;

public class WebAppManager {
	
	private Registry registry = new SQLRegistry();
	private NodeManager nodemanager;
	
	public WebAppManager(){

		nodemanager = NodeManager.getInstance();
		nodemanager.init();
	}
	
	public String getDirectoryListing(String path){

		System.out.println("INFO: Listing for directory: " + path);
		
		path = path.replace("//", "/");
		
		List<DFile> flist = registry.list(path);
		
		Map<String, String> finfo;
		List<Map<String, String>> filelist = new ArrayList<Map<String,String>>();

		for(DFile file: flist){
			finfo = new HashMap<String, String>();
			
			finfo.put("name", file.getName());
			finfo.put("size", "0");
			finfo.put("type", file.getType());
			finfo.put("path", file.getParent() + "/" + file.getName());

			if (file.getType().toLowerCase().equals("file")) {
				finfo.put("id", file.getId());
			} else {
				finfo.put("id", "");
			}
			filelist.add(finfo);
		}
		
		JSONObject reply = new JSONObject();
		reply.put("filelist", filelist);
		reply.put("path", path);

		return (reply.toString());
	}
	
	public String mkdir(String path){
		registry.mkdir(path);
		JSONObject reply = new JSONObject();
		reply.put("result", "OK");
		reply.put("path", path);

		return (reply.toString());
	}

	public String uploadfile(String cwd, String filename) throws IOException{
		
		String filepath = (cwd+"/"+filename).replace("//", "/");
		
		String fileid = registry.createUniqueFileName();
		
		File uploadedfile = new File("./tmp/" + filename);
		
		if (!uploadedfile.exists()){
			System.err.println("Uploaded file in tmp does not exist");
		}
		
		long uploadedFileSize = uploadedfile.length();
		
		Node datanode = nodemanager.getaDataNodeWithSpace(uploadedFileSize);

		if (datanode==null){
			System.err.println("FATAL: No DataNode given by node manager");
		}
				
		Path renamedtmpFile = Files.move(uploadedfile.toPath(), uploadedfile.toPath().resolveSibling(fileid));

		String tmpFilePath = uploadedfile.getAbsolutePath();
		
		System.out.println("File to be uploaded: " + tmpFilePath);
		
//		PyHttpClient pyClient = new PyHttpClient();
//		String dnReply = pyClient.upload(datanode.getURL()+"/upload", renamedtmpFile.toString());
		
		String dnReply = datanode.uploadFile(renamedtmpFile.toString());
		
		Files.delete(renamedtmpFile);
		
		JSONObject replyJson = null;
		
		try {
			replyJson = new JSONObject(dnReply);
			if (!replyJson.has("result")){
				System.err.println("ERROR: JSON reply from Datanode does not have key 'result'");
			}
		} catch (JSONException e) {
			System.err.println("ERROR while parsing: " + dnReply);
			e.printStackTrace();
		}

		
		registry.createMapping(cwd+"/"+filename, fileid, "file");
		registry.createMappingDN(fileid, datanode.getName());
		
		JSONObject reply = new JSONObject();
		Map<String, String> result = new HashMap<String, String>(); 
		result.put(filepath, "OK");
		reply.put("result", result);
		
		System.out.println(reply.toString());
		
		return reply.toString();
	}

	public String getFileUrl(String fileid) {

		JSONObject reply = new JSONObject();
		String[] dns = registry.getDatanodeForFileID(fileid);
		
		String datanode  = dns[0];
		
		if (datanode.isEmpty()){
			reply.put("result", "");
			return reply.toString();
		}
		
		Node node = nodemanager.getDatanodeByName(datanode);
		
		if (node == null){
			reply.put("result", "");
			return reply.toString();
		}
		
		reply.put("result", node.getURL() + "/" + fileid);
		return reply.toString();
	}

	public String rmFile(String filepath, String id) {

		registry.rmfile(filepath);
		
		String[] dns = registry.getDatanodeForFileID(id);

		for(String d: dns){
			Node node = nodemanager.getDatanodeByName(d);
			
			node.deleteFile(id);
			
			registry.rmmappingDN(id, d);
		}
		
		JSONObject reply = new JSONObject();
		reply.put("result", "");
		return reply.toString();
	}

	public String rmDir(String filepath) {
		
		boolean executed = registry.rmdir(filepath);

		JSONObject reply = new JSONObject();
		
		reply.put("result", String.valueOf(executed));
		
		return reply.toString();
	}
	
}
