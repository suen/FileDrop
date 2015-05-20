package com.filedrop.dfs.replication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.filedrop.dfs.namenode.Node;
import com.filedrop.dfs.namenode.NodeManager;
import com.filedrop.dfs.registry.model.DFile;
import com.filedrop.dfs.registry.model.Registry;

public class ReplicationManager implements Runnable {

	private static final int REPLICATION_FACTOR = 2;
	
	private NodeManager nodemanager;
	private Registry registry;
	private Thread thread;
	
	public ReplicationManager(NodeManager nodeManager, Registry registry) {
		this.nodemanager = nodeManager;
		this.registry = registry;
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void run() {
		
	}

	public void validateRegistryEntry(){
		List<DFile> list = registry.getTree();		
		for(DFile file: list ){
			String[] datanode = registry.getDatanodeForFileID(file.getId());
			
			if (datanode == null){
				System.out.println("ZOMBIE file detected " + 
						file.getPath());
				
				registry.rmfile(file.getPath());
				continue;
			}
			
			for(String dn: datanode){
				Node node = nodemanager.getDatanodeByName(dn);
				try {
					if (! node.exists(file.getId())) {
						System.out.println("False Entry detected " + 
								file.getParent() + "/" + file.getName());
						registry.rmmappingDN(file.getId(), dn);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			datanode = registry.getDatanodeForFileID(file.getId());
			if (datanode == null){
				System.out.println("No more datanode for file : " + 
						file.getPath());
			}
		}
	}
	
	public List<Map<String, String>> getReport(){
		validateRegistryEntry();
		List<DFile> list = registry.getTree();
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		for(DFile file: list ){
			String[] datanode = registry.getDatanodeForFileID(file.getId());

			Map<String, String> map = new HashMap<String, String>();
			
			map.put("path", file.getPath());
			map.put("id", file.getId());
			map.put("factor", String.valueOf(datanode.length));

			String dns = "";
			for(String dn: datanode){
				dns += "[" + dn + "] ";
			}
			map.put("datanodes", dns);
			result.add(map);
		}
		
		return result;

	}
	
	public void replicateFile(int factor){
		List<DFile> list = registry.getTree();		
		for(DFile file: list ){
			String[] dns = registry.getDatanodeForFileID(file.getId());
			
			if (dns == null){
				System.out.println("replicateFile(): ZOMBIE file detected " + 
						file.getPath());
				continue;
			}
			
			if (dns.length < factor){
				Node repSourceNode = nodemanager.getDatanodeByName(dns[0]);
				for (int i = dns.length; i<factor; i++) {
					
					//nodemanager.getDataNodesWithSpace(0);
					
				}
				System.out.println("");
			}
			
			for(String dn: dns){
				Node node = nodemanager.getDatanodeByName(dn);
				try {
					if (! node.exists(file.getId())) {
						System.out.println("False Entry detected " + 
								file.getParent() + "/" + file.getName());
						registry.rmmappingDN(file.getId(), dn);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			dns = registry.getDatanodeForFileID(file.getId());
			if (dns == null){
				System.out.println("No more datanode for file : " + 
						file.getPath());
			}
		
		}		
	}
	
	
	
	
}
