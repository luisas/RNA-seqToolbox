package plots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class LinePlot extends Plot{


	private Vector<Double> x;
	private Vector<Double> y;
	private String label;

	HashMap<Integer, String> labels = new HashMap<Integer, String>();
	StringBuilder command = new StringBuilder();
	int xr = 0 ;
	int yr = 0;
	int i = 0 ;

	public LinePlot(String title, String xlab, String ylab){

		super.setTitle(title);
		super.setXlab(xlab);
		super.setYlab(ylab);

	}


	public void addLine(Vector<Double> x , Vector<Double> y, String label, boolean plot){

		File tmp = getTmpFile();
		PlotUtils.writeVector(x, tmp); //write list to line
		PlotUtils.writeVector(y,tmp,true); //append=true
		this.command.append( String.format("x"+i+"<-scan(\"%s\",nlines=1,skip=0);",tmp));
		this.command.append( String.format("y"+i+"<-scan(\"%s\",nlines=1,skip=1);",tmp));
		labels.put(i, label);

		i++;


	}




	public void plot(String filename) throws FileNotFoundException, IOException
	{
		this.command.insert(0,String.format("jpeg(\"%s\",  width = 850, height = 800);", filename) );
		this.command.append("par(xpd = T, mar = par()$mar + c(0,0,0,7)); ");
		StringBuilder rangeX = new StringBuilder();
		StringBuilder rangeY = new StringBuilder();
		this.command.append("col=rainbow("+i+"); ");
		rangeX.append("xr<-range(");
		rangeY.append("yr<-range(");

		String prefix = "";
		for(int j = 0 ; j<i; j++){
			rangeX.append(prefix+"x"+j);
			rangeY.append(prefix+"y"+j);
			prefix=",";
		}
		rangeX.append("); ");
		rangeY.append("); ");
		this.command.append(rangeX);
		this.command.append(rangeY);

		for(int j = 0 ; j<i; j++){
			if(j==0){
				this.command.append( String.format("plot(x"+j+",y"+j+",type=\"l\",xlim=xr, ylim=yr);"));
			}else{
				//this.command.append("points(x"+j+",y"+j+",lty="+j+", col = col["+j+"]); ");
				this.command.append("lines(x"+j+",y"+j+",lty=1, col = col["+j+"]); ");
			}

		}
		String forlegend = "";
		String pre = "";
		for(int d= 0 ; d< labels.size(); d++){
			forlegend+=pre;
			forlegend+="\"";
			forlegend+=labels.get(d);
			forlegend+="\"";
			pre = ",";
		}

		this.command.append("l<-c(" + forlegend+ "); ");
		this.command.append("legend(\"bottom\", l,text.col=col);");
		this.command.append( String.format("title(main=\"%s\", xlab=\"%s\",ylab=\"%s\");",super.title, super.xlab, super.ylab));
		this.command.append( "dev.off();");


		System.out.println(this.command);
		RExecutor r =new RExecutor(this.command.toString());
		Thread t = new Thread(r);
		t.start();
		try{
			t.join();
		}
		catch(InterruptedException e){
			throw new RuntimeException("R did not exit"+" properly!");
		}
	}



//
//	String generateCommand(String filename) throws FileNotFoundException, IOException
//	{
//
//
//
//
//		return command.toString();
//	}





	private File getTmpFile() {
		// TODO Auto-generated method stub

		File tempDir = new File(Config.getConfig("tmpdir",null));
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


	@Override
	String generateCommand(String string) {
		// TODO Auto-generated method stub
		return null;
	}






}
