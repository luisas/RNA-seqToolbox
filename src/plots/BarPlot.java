package plots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class BarPlot extends Plot{
	private Vector<Double> x;
	private Vector<String> labels;
	private String label;
	private String color = "blue";




	public BarPlot(String title, String xlab, String ylab, Vector<Double> x, Vector<String> labels){

		super.setTitle(title);
		super.setXlab(xlab);
		super.setYlab(ylab);
		this.x = x;
		this.labels=labels;

	}
	
	public BarPlot(String title, String xlab, String ylab, Vector<Double> x, Vector<String> labels, String color){

		super.setTitle(title);
		super.setXlab(xlab);
		super.setYlab(ylab);
		this.x = x;
		this.labels=labels;
		this.color = color; 

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
		
		
		return generateCommandSummary(filename); 

	}
	
	
	public String generateCommandBasic(String filename){
		File tmp = getTmpFile();
		PlotUtils.writeVector(this.x, tmp);
		String labelString = PlotUtils.writeLabels(this.labels);
		StringBuilder command = new StringBuilder();
		command.insert(0,String.format("jpeg(\"%s\",  width = 850, height = 800);", filename) );
		command.append(String.format("x<-scan(\"%s\",nlines=1,skip=0);",tmp));
		command.append(String.format("data <- matrix(x,ncol=%d,byrow=TRUE);",this.x.size()));
		command.append("data<-as.table(data);");
		command.append(String.format("colnames(data)<-%s;",labelString));
		command.append("barplot(data,");
		command.append( String.format("col=\"%s\",main=\"%s\", xlab=\"%s\",ylab=\"%s\");",this.color, super.title, super.xlab, super.ylab));
		command.append( "dev.off();");
		return command.toString();
		
	}


	public String generateCommandSummary(String filename){
		File tmp = getTmpFile();
		PlotUtils.writeVector(this.x, tmp);
		String labelString = PlotUtils.writeLabels(this.labels);
		StringBuilder command = new StringBuilder();
		command.insert(0,String.format("jpeg(\"%s\",  width = 850, height = 800);", filename) );
		command.append(String.format("x<-scan(\"%s\",nlines=1,skip=0);",tmp));
		command.append(String.format("data <- matrix(x,ncol=3,byrow=TRUE);",this.x.size()));
		command.append("data<-as.table(data);");
		command.append("rownames(data)<-c(\"Rest\",\"no Mutations\",\"Region with a length > 5\");");

		command.append(String.format("colnames(data)<-%s;",labelString));
		command.append("barplot(data,legend.text = TRUE, ");
		command.append( String.format("col=c(\"green\",\"blue\",\"red\"),main=\"%s\", xlab=\"%s\",ylab=\"%s\");", super.title, super.xlab, super.ylab));
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
	public Vector<String> getLabels() {
		return labels;
	}
	public void setY(Vector<String> labels) {
		this.labels = labels;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}


}
