package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class NodeManager implements Runnable {

	private List<Node> nodes;
	private int dnCount = 0;
	private final String configDirName = "./config/datanode";

	private Thread nodeThread ;

	public NodeManager(){

	}

	
	public void init(){
		nodeThread = new Thread(this);
		nodeThread.setDaemon(true);
		nodeThread.start();
	}

	private void readNodeConfigs() throws IOException{
		nodes = new ArrayList<Node>();
		File configDir = new File(configDirName);

		if (!configDir.exists()){
			System.err.println("[NodeManager] FATAL: Config directory does not exists");
			return;
		}

		String[] configFileList = configDir.list(); 

		for(String configfn: configFileList){
			readConfig(configfn);
		}

		if (dnCount == 0 || dnCount != nodes.size()) {
			dnCount = nodes.size();
			System.out.println("[NodeManager] INFO: New DataNode List");
			printstat();
		}
	}

	private void readConfig(String configfn) throws IOException{
		
		if (!configfn.endsWith(".conf"))
			return;
		
		File configfile = new File(configDirName + "/" + configfn);
		if (!configfile.exists()){
			System.err.println("[NodeManager] FATAL: Config file  '" + configfn + "' does not exists");
			return;
		}

		Properties prop = new Properties();
		//String testconfig = "./config/datanode1.properties";

		InputStream iStream = new FileInputStream(configfile);

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configfile.getAbsolutePath());

		if(iStream==null){
			System.err.println("[NodeManager] FATAL: Config file stream failed");
			return;
		}
		prop.load(iStream);

		String name = prop.get("name").toString();
		String ip = prop.get("ip").toString();
		String port = prop.get("port").toString();
		String totalspace = prop.get("space").toString().trim();
		Node node = new Node(ip, name, port, Long.valueOf(totalspace));
		nodes.add(node);
	}

	public Node getDatanodeByName(String name){
		for(Node node: nodes){
			if (node.getName().equals(name))
				return node;
		}
		System.out.println("[NodeManager] ERROR: Datanode " + name + " not found");
		return null;
	}

	public void pingDataNodes(){

		//System.out.println("[NodeManager] pinging ");
		
		List<Node> newNodeList = new ArrayList<Node>();
		//List<Integer> deadNodes = new ArrayList<Integer>();
		for(Node node: nodes){
			long size = -1;

			try {
				size = node.getOccupiedSpace();
			} catch (Exception e) {
				//System.out.println("[NodeManager] size "+size);
				e.printStackTrace();
			}

			if (size == -1){
				//deadNodes.add(nodes.indexOf(node));
				//System.out.println("[NodeManager] ERREUR: " + node.getIdentifier() + " DEAD (" + nodes.indexOf(node) + ", " + node.getIdentifier() + ")");
				continue;
			} else {
				//System.out.println("INFO: " + node.getIdentifier() + " alive ("+size+")");
				newNodeList.add(node);
			}
		}
		
//		System.out.println("[NodeManager] INFO: New list ("+newNodeList.size() +")"
//				+ "old list ("+nodes.size() +")");

		/*
		for(Integer nodeIndex: deadNodes){
			System.out.println("[NodeManager] Removing "+nodes.get(nodeIndex).getIdentifier() + " (" + nodes.size() + ")" );
			nodes.remove(nodeIndex);
			System.out.println("[NodeManager] Removed "+nodes.get(nodeIndex).getIdentifier() + " (" + nodes.size() + ")" );
		}
		*/
		this.nodes = newNodeList;
	}


	private void printstat(){
		for(Node node: nodes){
			System.out.println("\tNode: " + node.getIdentifier() + " size: " + node.getOccupiedSpace());
		}
	}

	public List<Node> getDatanodes(){
		return nodes;
	}

	public Node getaDataNodeWithSpace(long size){

		List<Node> freenodes = getDataNodesWithSpace(size);

		if (freenodes.size()==0)
			return null;
		if (freenodes.size()==1)
			return freenodes.get(0);

		Random random = new Random();
		int rand = random.nextInt(freenodes.size());
		return freenodes.get(rand);
	}

	public List<Node> getDataNodesWithSpace(long size){

		List<Node> freenodes = new ArrayList<Node>();
		for(Node node: nodes){
			long freespace = node.getTotalSpace() - node.getOccupiedSpace();

			if (size <= freespace)
				freenodes.add(node);
		}
		return freenodes;
	}



	@Override
	public void run() {
		System.out.println("[NodeManager] initialized");
		try {
			while(true) {
				readNodeConfigs();
				pingDataNodes();
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			System.out.println("[NodeManager] FATAL: NodeManager Thread dead");
		}
	}

}
