package plots;

import java.util.HashMap;


public class Config {


	static HashMap<String,String> configurations = new HashMap<String,String>();

	public static void init(){
		configurations.put("R","/usr/local/bin/R");
		configurations.put("tmpdir","/Users/luisasantus/temp");

	}
	public static String getConfig(String string){

		init();

		return configurations.get(string);

	}


}
