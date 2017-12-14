package plots;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class Config {


	static HashMap<String,String> configurations = new HashMap<String,String>();

	public static void init(){
		configurations.put("R","/usr/bin/R");
		configurations.put("tmpdir","/home/s/santus/Desktop/temp");

	}
	
	
	public static void init(String filename) {
		
		if(filename == null) {
			init();
		}else {
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
	
				String currentLine;
				while ((currentLine = br.readLine()) != null ) {
					String[] splittedLine = currentLine.split("\t");
					configurations.put(splittedLine[0],splittedLine[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
//	public static String getConfig(String string){
//
//		init();
//		return configurations.get(string);
//	}
	
	public static String getConfig(String string,String file) {
		init(file);
		return configurations.get(string);
		
	}


}
