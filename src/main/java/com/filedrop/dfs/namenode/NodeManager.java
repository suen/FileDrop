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
		//System.out.println("INFO: New DataNode '" +node.getIdentifier() + "'" );
	}

	public Node getDatanodeByName(String name){
		for(Node node: nodes){
			if (node.getName().equals(name))
				return node;
		}
		System.out.println("ERROR: Datanode " + name + " not found");
		return null;
	}

	public void pingDataNodes(){

		List<Integer> deadNodes = new ArrayList<Integer>();
		for(Node node: nodes){
			long size = 0;

			try {
				size = node.getOccupiedSpace();
			} catch (Exception e) {
				
			}

			if (size==0){
				deadNodes.add(nodes.indexOf(node));
				System.out.println("ERREUR: " + node.getIdentifier() + " DEAD");
				continue;
			}

			//System.out.println("INFO: " + node.getIdentifier() + " alive");
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
		try {
			readNodeConfigs();
			pingDataNodes();
			Thread.sleep(10000);
			run();

		} catch (Exception e) {
		}
	}


	public static void main(String[] args) throws IOException {
		NodeManager manager = new NodeManager();
		manager.readNodeConfigs();
		manager.pingDataNodes();
		manager.printstat();
//
//		Node node = manager.getaDataNodeWithSpace(8331022771L);
//		System.out.println("Node with free space " + node.getIdentifier());
	}

}
