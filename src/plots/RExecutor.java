package plots;

import java.io.IOException;

public class RExecutor implements Runnable {

	String rCommand;
	String configFilePath = null; 

	public RExecutor(String c) {
		this.rCommand = c;
	}
	public RExecutor(String c, String configFilePath) {
		this.rCommand = c;
		this.configFilePath = configFilePath;
	}

	@Override
	public void run()
	{
	String R = Config.getConfig("R", configFilePath); // or Rscript
	try {
		new ProcessBuilder(R, "-e",rCommand).start();
	} catch (IOException e) {

		e.printStackTrace();
	}
	}
	
//	public void run(String name)
//	{
//	String R = Config.getConfig("R",name); // or Rscript
//	try {
//		new ProcessBuilder(R, "-e",rCommand).start();
//	} catch (IOException e) {
//
//		e.printStackTrace();
//	}
//	}

}
