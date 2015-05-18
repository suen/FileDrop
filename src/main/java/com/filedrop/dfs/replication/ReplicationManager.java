package com.filedrop.dfs.replication;

import java.util.List;

import com.filedrop.dfs.namenode.Node;
import com.filedrop.dfs.namenode.NodeManager;
import com.filedrop.dfs.registry.model.DFile;
import com.filedrop.dfs.registry.model.Registry;

public class ReplicationManager implements Runnable {

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

	private void queryRegistry(){
		
		nodemanager.getDatanodes();
		
		List<DFile> list = registry.list("/");

		for(DFile file: list ){
			String[] datanode = registry.getDatanodeForFileID(file.getId());
			
			if (datanode == null){
				System.out.println("ZOMBIE file detected " + 
							file.getParent() + "/" + file.getName());
			}
			
		}
		
		
	}
	
	
	
	
}
