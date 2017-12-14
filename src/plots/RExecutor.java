package plots;

import java.io.IOException;

public class RExecutor implements Runnable {

	String rCommand;
	String configFilePath = null;
	boolean rscript = false  ;
	String[] params = null ;

	public RExecutor(String c) {
		this.rCommand = c;
	}
	public RExecutor(String configFilePath,String[] params, boolean rscript) {
		this.configFilePath = configFilePath;
		this.params=params;
		this.rscript = rscript;
	}

	@Override
	public void run()
	{
	String R = Config.getConfig("R", configFilePath);


	String script = Config.getConfig("diffscript", configFilePath);// or Rscript
	try {
		try {
			//Runtime.getRuntime().exec("/usr/bin/Rscript "+rCommand);
			//Runtime.getRuntime().exec( new String[]{"/usr/bin/Rscript","/home/proj/biosoft/praktikum/genprakt/DifferentialExpression/scripts/de_rseq.R",

//					"/home/s/santus/Desktop/out/exprs.txt","/home/s/santus/Desktop/out/p_data.txt","/home/s/santus/Desktop/out/f_data.txt limma","/home/s/santus/Desktop/out/limma_temp.out"
//
//
//			});
			if(!rscript){
				new ProcessBuilder(R,"-e",rCommand).start().waitFor();
			}
			else{
				String[] completeCommand  = new String[params.length +2];
				completeCommand[0]=R;
				completeCommand[1]=script;
				for(int i = 0 ; i < params.length ; i ++){

					completeCommand[i+2] = params[i];
				}

				Process p = new ProcessBuilder(completeCommand).start();
						//"/home/s/santus/Desktop/out/exprs.txt","/home/s/santus/Desktop/out/p_data.txt","/home/s/santus/Desktop/out/f_data.txt","limma","/home/s/santus/Desktop/out/limmma_temp.out").start();
				p.waitFor();
			}


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
