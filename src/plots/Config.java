package plots;

import java.util.HashMap;


public class Config {


	static HashMap<String,String> configurations = new HashMap<String,String>();

	public static void init(){
		configurations.put("R","/usr/bin/R");
		configurations.put("tmpdir","/home/s/santus/Desktop/temp");

	}
	public static String getConfig(String string){

		init();

		return configurations.get(string);

	}


}
