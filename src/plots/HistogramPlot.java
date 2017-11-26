package plots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import readSimulator.Utils;

public class HistogramPlot extends Plot {

	private Vector<Double> x;
	private Vector<Double> y;
	private String label;
	private StringBuilder command;
	private int breaks; 
	



	public HistogramPlot(String title, String xlab, String ylab, Vector<Double> x, int breaks){

		super.setTitle(title);
		super.setXlab(xlab);
		super.setYlab(ylab);
		this.x = x;
		this.breaks = breaks; 

	}


	public void plot(String filename) throws FileNotFoundException, IOException
	{
		//EXECUTE!
		System.out.println(generateCommand(filename));
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



	public String generateCommand(String filename){
		File tmp = getTmpFile();
		PlotUtils.writeVector(this.x, tmp);
		//PlotUtils.writeVector(this.y,tmp,true);
		StringBuilder command = new StringBuilder();


		command.insert(0,String.format("jpeg(\"%s\",  width = 850, height = 800);", filename) );
		command.append(String.format("x<-scan(\"%s\",nlines=1,skip=0);",tmp));
		command.append("h<-hist(x,col=\"red\","); 
		command.append( String.format("main=\"%s\", xlab=\"%s\",ylab=\"%s\",breaks=%d);",super.title, super.xlab, super.ylab,this.breaks));
		command.append("xfit<-seq(min(x),max(x),length=40) ; yfit<-dnorm(xfit,mean=mean(x),sd=sd(x)) ; yfit <- yfit*diff(h$mids[1:2])*length(x) ; lines(xfit, yfit, col=\"blue\", lwd=2); "); 
		command.append( "dev.off();");

		return command.toString();
	}







	private File getTmpFile() {
		// TODO Auto-generated method stub

		File tempDir = new File(Config.getConfig("tmpdir"));
		File tmp = null;
		try {
			tmp = File.createTempFile("TEMP",".tsv", tempDir );
			//tmp.deleteOnExit();

		} catch (IOException e) {
			System.out.println("Error Generating temp file");
			e.printStackTrace();
		}


		return tmp;
	}







	public Vector<Double> getX() {
		return x;
	}
	public void setX(Vector<Double> x) {
		this.x = x;
	}
	public Vector<Double> getY() {
		return y;
	}
	public void setY(Vector<Double> y) {
		this.y = y;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}








}
