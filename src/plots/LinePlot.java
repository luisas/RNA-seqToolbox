package plots;

import java.io.File;
import java.io.IOException;
import java.util.Vector;


public class LinePlot extends Plot{


	private Vector<Double> x;
	private Vector<Double> y;
	private String label;


	public LinePlot(String title, String xlab, String ylab){

		super.setTitle(title);
		super.setXlab(xlab);
		super.setYlab(ylab);

	}


	public void addLine(Vector<Double> x , Vector<Double> y, String label){



	}




	public void plot(String filename)
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
		command.append( String.format("jpeg(\"%s\");", filename));
		command.append( String.format("x<-scan(\"%s\",nlines=1,skip=0);",tmp));
		command.append( String.format("y<-scan(\"%s\",nlines=1,skip=1);",tmp));
		command.append( String.format("plot(x,y,type=\"o\");"));
		command.append( String.format("title(main=\"%s\", xlab=\"%s\",ylab=\"%s\");",super.title, super.xlab, super.ylab));
		command.append( "dev.off()");
		System.out.println(command);
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