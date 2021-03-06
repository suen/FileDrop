package com.filedrop.dfs.httpclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PyHttpClient {

	public String uploads(String url, String filepath){
	
    try {
		Process process = Runtime.getRuntime().exec(
				"python clientpy.py " + url + " " + filepath);
		InputStream stdout = process.getInputStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		
		String output = "";
		String line;
		while((line = br.readLine())!=null){
			if (output=="")
				output = " ";
			else
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
		
		py.uploads("http://localhost:8080/upload", "/d/ent/movies/A.Walk.Among.The.Tombstones.2014.FANSUB.VOSTFR.HDRiP.CROPPED.XviD.AC3-NIKOo-ZT.avi");
	}
	
}
