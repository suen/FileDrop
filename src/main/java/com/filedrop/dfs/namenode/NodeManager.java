package com.filedrop.dfs.namenode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NodeManager {

	private List<Node> nodes = new ArrayList<Node>();	
	private final String configDirName = "./config";
	
	public NodeManager(){
	
	}
	
	private void readNodeConfigs() throws IOException{
		
		File configDir = new File(configDirName);
		
		if (!configDir.exists()){
			System.err.println("FATAL: Config directory does not exists");
			return;
		}
		
		String[] configFileList = configDir.list(); 
		
		for(String configfn: configFileList){
			readConfig(configfn);
		}
	}
	
	private void readConfig(String configfn) throws IOException{
		File configfile = new File(configDirName + "/" + configfn);
		if (!configfile.exists()){
			System.err.println("FATAL: Config file  '" + configfn + "' does not exists");
			return;
		}
		
		Properties prop = new Properties();
		//String testconfig = "./config/datanode1.properties";
		
		InputStream iStream = new FileInputStream(configfile);
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configfile.getAbsolutePath());
		
		if(iStream==null){
			System.err.println("FATAL: Config file stream failed");
			return;
		}
		prop.load(iStream);
		
		String name = prop.get("name").toString();
		String ip = prop.get("ip").toString();
		String port = prop.get("port").toString();
		String totalspace = prop.get("space").toString();
		Node node = new Node(ip, name, port, Long.valueOf(totalspace));
		nodes.add(node);
		System.out.println("INFO: New DataNode '" +node.getIdentifier() + "'" );
	}
	
	public void pingDataNodes(){
		
		List<Integer> deadNodes = new ArrayList<Integer>();
		for(Node node: nodes){
			long size = node.getOccupiedSpace();
			
			if (size==0){
				deadNodes.add(nodes.indexOf(node));
				System.out.println("INFO: " + node.getIdentifier() + " DEAD");
				continue;
			}

			System.out.println("INFO: " + node.getIdentifier() + " alive");
		}
		
		for(Integer nodeIndex: deadNodes){
			nodes.remove(nodeIndex);
		}
	}
	
	
	public void printstat(){
		for(Node node: nodes){
			System.out.println("Node: " + node.getIdentifier() + " size: " + node.getOccupiedSpace());
		}
	}
	
	public List<Node> getDatanodes(){
		return nodes;
	}
	
	public Node getaDataNodeWithSpace(long size){
		for(Node node: nodes){
			long freespace = node.getTotalSpace() - node.getOccupiedSpace();
			
			if (size <= freespace)
				return node;
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		NodeManager manager = new NodeManager();
		manager.readNodeConfigs();
		manager.pingDataNodes();
		manager.printstat();
		
		Node node = manager.getaDataNodeWithSpace(8331022771L);
		System.out.println("Node with free space " + node.getIdentifier());
	}

	
}
