package plots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Vector;



public class RunnerPlot {



	public static void main(String[] args) throws IOException, InterruptedException {


		plotExercise2();


	}



	public static void plotExercise2(){

	}




	//Needed for exercise one : to be checked that some dirs actually exist!
	static String folder = "/home/proj/biosoft/praktikum/genprakt/gtfs/";
	static String outputfolder = "/home/s/santus/Desktop/results";
	static String jar = "/home/s/santus/Desktop/es1.jar";
	static String forPlotFile = folder+"forPlot.csv";
	static StringBuilder commandExon = new StringBuilder();


	public static void createTsvs(String folder) throws InterruptedException, IOException{
		ArrayList<String> gtfs = getFileNames(folder);

		String path = outputfolder;

		for(String gtf: gtfs){


			String name = gtf.replace(".gtf", ".tsv");

			try {


				Process proc  =Runtime.getRuntime().exec("java -jar "+jar+" -gtf "+folder+gtf+" -o "+path+"/"+name);
				proc.waitFor();
				  java.io.InputStream in = proc.getInputStream();
				  java.io.InputStream err = proc.getErrorStream();

				    byte b[]=new byte[in.available()];
				    in.read(b,0,b.length);
				    System.out.println(new String(b));

				    byte c[]=new byte[err.available()];
				    err.read(c,0,c.length);
				    System.out.println(new String(c));

				    System.out.println(proc);

			} catch (IOException e) {
				System.out.println("Problem with calling the jar file");
				e.printStackTrace();
			}
		}


	}

	public static ArrayList<String> getFileNames (String folder) throws IOException, InterruptedException{

		ArrayList<String> list = new ArrayList<String>();

		String command = "ls "+ folder ;


		Process p = Runtime.getRuntime().exec(command);
	    p.waitFor();

	    BufferedReader reader =
	         new BufferedReader(new InputStreamReader(p.getInputStream()));


	    String line = "";
	    while ((line = reader.readLine())!= null) {
	    		list.add(line);
	    		System.out.println(line);
	    }

	    return list;

	}

	public static void createDir(String dir)
	{

		File theDir = new File(dir);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    }
		    catch(SecurityException se){
		        //handle it
		    }
		    if(result) {
		        System.out.println("DIR created");
		    }
		}
	}


	public static void plotExerciseOne() throws InterruptedException, IOException{
		commandExon.append( String.format("jpeg(\"%s\");", "skipped_exons.jpg"));

		// call for each file
		// compute the tsv file for each gtf file
		createTsvs(folder);

		ArrayList<String> tsvs = getFileNames(outputfolder);





		LinePlot lp_exons = new LinePlot("Cumulative distribution of skipped Exons", "Skipped Exons", "Number Of Events");
		LinePlot lp_bases = new LinePlot("Cumulative distribution of skipped Bases", "Skipped Bases", "Number Of Events");

		boolean firstFile =  true;
		for(String tsv : tsvs){

			//System.out.println(tsv);

			Vector<Double> vectorExons = new Vector<Double>();
			Vector<Double> vectorBases = new Vector<Double>();
			boolean first = true;


			try (BufferedReader br = new BufferedReader(new FileReader(outputfolder+"/"+tsv))) {
				System.out.println();

				String currentLine;

				while ((currentLine = br.readLine()) != null ) {

					if(first){
						first = false;

					}else{

					String[] Splitted = currentLine.split("\t");
					vectorExons.add(Double.parseDouble(Splitted[11]));
					vectorBases.add(Double.parseDouble(Splitted[13]));
					}

				}

			}

			Pair<Vector<Double>,Vector<Double>> pExons = PlotUtils.ecdf(vectorExons);
			Pair<Vector<Double>,Vector<Double>> pBases = PlotUtils.ecdf(vectorBases);

			System.out.println(pExons.first);
			System.out.println(pExons.second);
			if(firstFile){
				lp_exons.addLine(pExons.first, pExons.second, tsv, true);
				lp_bases.addLine(pBases.first, pBases.second, tsv, true);
				firstFile = false ;
			}else{

				lp_exons.addLine(pExons.first, pExons.second, tsv, false);
				lp_bases.addLine(pBases.first, pBases.second, tsv, false);
			}

		}



		lp_exons.plot("/home/s/santus/Desktop/skipped_exons.jpg");
		lp_bases.plot("/home/s/santus/Desktop/skipped_bases.jpg");
	}


}
