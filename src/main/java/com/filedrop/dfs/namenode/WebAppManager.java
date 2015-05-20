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
import com.filedrop.dfs.replication.ReplicationManager;

public class WebAppManager {
	
	private Registry registry = new SQLRegistry();
	private NodeManager nodemanager;
	private ReplicationManager repManager;
	
	public WebAppManager(){

		nodemanager = NodeManager.getInstance();
		nodemanager.init();
		
		repManager = new ReplicationManager(nodemanager, registry);
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
			finfo.put("size", file.getSize());
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
	
	public String getReplicationStatus(){
		
		List<Map<String, String>> listfiles = repManager.getReport();
		
		JSONObject reply = new JSONObject();
		reply.put("filelist", listfiles);
		reply.put("result", "OK");
	
		System.out.println(reply.toString());
		return reply.toString();
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
		
		JSONObject replyPyUpload = null;
		JSONObject reply = new JSONObject();
		
		try {
			replyPyUpload = new JSONObject(dnReply);
			if (!replyPyUpload.has("result")){
				String reason = "ERROR: JSON reply from Datanode does not have key 'result'";
				System.err.println(reason);
				reply.put("error", reason);
				return reply.toString();
			}
		} catch (JSONException e) {
			String reason = "ERROR while parsing: " + dnReply;
			reason += "\nDatenode: " + datanode.getName() + "\n";
			System.err.println(reason);
			reply.put("error", reason);
			return reply.toString();
		}

		DFile fileEntry = new DFile();
		fileEntry.setName(filename);
		fileEntry.setParent(cwd);
		fileEntry.setId(fileid);
		fileEntry.setType("file");
		fileEntry.setSize(String.valueOf(uploadedFileSize));
		
		registry.insertFile(fileEntry);
		registry.createMappingDN(fileid, datanode.getName());

		Files.delete(renamedtmpFile);
		
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

	public String getDataNodeStatus() {
		List<Node> nodes = nodemanager.getDatanodes();

		JSONObject reply = new JSONObject();
		
		List<String> dinfos = new ArrayList<String>();
		for(Node node: nodes){
			String dinfo = node.getIdentifier();
			dinfos.add(dinfo);
		}
		
		reply.put("datanodes", dinfos);
		reply.put("result", "OK");
		
		System.out.println(reply.toString());
		return reply.toString();
	}
	
}
