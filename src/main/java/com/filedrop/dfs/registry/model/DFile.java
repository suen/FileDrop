package com.filedrop.dfs.registry.model;

public class DFile {

	private String name;
	private String type;
	private String parent;
	private String id;
	
	public DFile(String name, String type) {
		super();
		this.name = name;
		this.type = type;
		this.setParent("");
	}
	
	public DFile(String name, String type,String parent) {
		super();
		this.name = name;
		this.type = type;
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String path) {
		this.parent = path;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString(){
		return "[File.name="+getName()+", File.parent="+getParent()+", File.type="+getType()+"]\n";
	}

}

