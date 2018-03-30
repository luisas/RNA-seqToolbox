package plots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Vector;



public class RunnerPlot {

	static boolean fancy = true ; 

	public static void main(String[] args) throws IOException, InterruptedException {


		//plotExercise2("/home/s/santus/Desktop");
		plotExerciseOne();

	}



	public static void plotExercise2(String outputDir) throws FileNotFoundException, IOException{

		//file with all information: to be changed everytime!
		String input = "/home/s/santus/Desktop/output/read.mappinginfo"; 
		HashMap<String,Vector<Double>> results = retrieveInformationPlot2(input);

		//results.get("fragmentLength")
		HistogramPlot histoFragments = new HistogramPlot("Fragment length distribution", "Fragment length"," Count", results.get("fragmentLength"),10);
		histoFragments.plot(outputDir+"/fragmentDistribution.jpeg");

		HistogramPlot histoMut = new HistogramPlot("Mutation distribution pro read", "Number of mutations"," Count", results.get("mutations"),5);
		histoMut.plot(outputDir+"/mutationDistribution.jpeg");

		Vector<String> myLabels = new Vector<String>();
		myLabels.add("All reads");
		myLabels.add("non splitted");
		if(!fancy){
			myLabels.add("non splitted & no mutated");
		}
		myLabels.add("splitted");
		
		if(!fancy){
			myLabels.add("splitted and no mutated");
			myLabels.add("split, no mutated and with at least 5 length regions");
		}
		

		BarPlot barp = new BarPlot("Summary of file "+ input+"","","",results.get("barPlot"),myLabels,"blue" );
		barp.plot(outputDir+"/summary.jpeg");


	}

	public static HashMap<String,Vector<Double>> retrieveInformationPlot2(String filename){

		HashMap<String,Vector<Double>> result = new HashMap<String,Vector<Double>>();


		String a = "";

		String[] b = a.split("-");

		System.out.println(b.length);

		System.out.println(b[0]=="");
		/*
		 * GET FRAGMENTS for fragment length distribution
		 */
		//read the file and save all the fragments
		Vector<Double> fragments = new Vector<Double>();
		Vector<Double> mutations = new Vector<Double>();
		Vector<Double> barPlot = new Vector<Double>();

		double countRead = 0.0;
		double nonSplit = 0.0;
		double nonSplitnoMis = 0.0;
		double split = 0.0;
		double splitnoMis = 0.0;
		double atLeast =0.0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			String[] splittedLine;
			while ((currentLine = br.readLine()) != null ) {
				if(!currentLine.startsWith("read")) {
					splittedLine = currentLine.split("\t",-1);

					//FRAGMENT LENGTH
					double startFragment = Integer.parseInt(splittedLine[4].split("-")[0]);
					double stopFragment = Integer.parseInt(splittedLine[5].split("-")[1]);
					fragments.add(stopFragment-startFragment+1);


					//MUTATIONS
					double fwmut;
					double rwmut;

					fwmut  = splittedLine[8].split(",").length;
					rwmut  = splittedLine[9].split(",").length;
					if(splittedLine[8].length() == 0){ fwmut=0;  }
					if(splittedLine[9].length() == 0){ rwmut=0; }
					mutations.add(rwmut);
					mutations.add(fwmut);


					//FW

					if(splittedLine[6].split("\\|").length > 1 ){
						split++;
						if(fwmut==0){
							splitnoMis++;
							if(atLeast5(splittedLine[6])){atLeast++;};
						}

					}else{
						nonSplit++;
						if(fwmut==0){
							nonSplitnoMis++;
						}
					}

					//BW
					if(splittedLine[7].split("\\|").length > 1 ){
						split++;
						if(rwmut==0){
							splitnoMis++;
							if(atLeast5(splittedLine[6])){atLeast++;};
						}
					}else{
						nonSplit++;

						if(rwmut==0){

							nonSplitnoMis++;
						}
					}



					//READS
					countRead++;
					countRead++;


				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		result.put("fragmentLength",fragments);
		result.put("mutations", mutations);

		barPlot.add(countRead);
		
		if(fancy){
			barPlot.add(nonSplit-nonSplitnoMis);
			barPlot.add(split-splitnoMis-atLeast);
			barPlot.add(0.0);		
		}
		else{
			barPlot.add(nonSplit);
			barPlot.add(split);
			
		}
		

		if(fancy){
			barPlot.add(nonSplitnoMis);
			barPlot.add(splitnoMis-atLeast);
			barPlot.add(0.0);
			barPlot.add(0.0);			
		}else{
			barPlot.add(nonSplitnoMis);
			barPlot.add(splitnoMis);
		}
		barPlot.add(atLeast);
		result.put("barPlot", barPlot);


		return result;
	}


	public static boolean atLeast5(String string){


		for(String region : string.split("\\|")){

			if(!((Integer.parseInt(region.split("-")[1])-Integer.parseInt(region.split("-")[0])) > 5)){
				return false;
			}

		}
		return true;

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
