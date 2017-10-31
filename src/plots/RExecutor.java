package plots;

import java.io.IOException;

public class RExecutor implements Runnable {

	String rCommand;
	
	public RExecutor(String c) {
		this.rCommand = c;
	}
	
	@Override
	public void run()
	{
	String R = "/usr/local/bin/R";
	//String R = Config.getConfig("R"); // or Rscript
	try {
		Process p = new ProcessBuilder(R, "-e",rCommand).start();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//TODO: handle additional things like streams
	}

}
