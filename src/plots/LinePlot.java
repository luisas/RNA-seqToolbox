package plots;

import java.io.File;
import java.util.Vector;

public class LinePlot extends Plot{
	
	
	Vector<Double> x;
	Vector<Double> y;
	
	void plot(String filename)
	{
		RExecutor r =new RExecutor(generateCommand(filename));
		Thread t = new Thread(r);
		t.start();
		try{
			t.join();
		} 
		catch(InterruptedException e){
			throw new RuntimeException("R did not exit"+" properly!");
		}
	}
	
	
	
	
	String generateCommand(String filename)
	{
		File tmp = getTmpFile();
		PlotUtils.writeVector(this.x, tmp); //write list to line
		PlotUtils.writeVector(this.y,tmp,true); //append=true
		StringBuilder command = new StringBuilder();
		command.append( String.format("pdf(\"%s\");", filename));
		command.append( String.format("x<-scan(\"%s\",nlines=1,skip=0);",tmp));
		command.append( String.format("y<-scan(\"%s\",nlines=1,skip=1);",tmp));
		command.append( String.format("plot(x,y,ann=F);"));
		command.append( String.format("title(main=\"%s\", xlab=\"%s\",ylab=\"%s\");",super.title, super.xlab, super.ylab));
		command.append( "dev.off()");
		return command.toString();
	}




	private File getTmpFile() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	String generateCommand() {
		// TODO Auto-generated method stub
		return null;
	}



}
