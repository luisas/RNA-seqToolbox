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
	String R = Config.getConfig("R"); // or Rscript
	try {
		new ProcessBuilder(R, "-e",rCommand).start();
	} catch (IOException e) {

		e.printStackTrace();
	}
	}

}
