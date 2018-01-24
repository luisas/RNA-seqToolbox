package goSetEnrichment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import plots.Pair;

public class goRunner {

	static boolean test = false; 
	static String obo; 
	static String root; 
	static String mapping; 
	static String mappingtype; 
	static String overlapout ="";
	static String enrich; 
	static String o ; 
	static int minsize; 
	static int maxsize; 

	static DAG dag;  
	//Gene-->List of GO:terms
	static HashMap<String,List<String>> mappingMap; 
	
	static HashMap<String,Pair<Double,Boolean>> enrichMap; 
	
	static List<String> eIds; 
	
	public static void main(String[] args ) {
		
		readCommandLine(args);
		
		
		Pair<DAG, HashMap<String, Node>> pairDag=Parsers.parseObo(obo,root);
		HashMap<String, Node> id2node = pairDag.second;
		dag = pairDag.first;
		
		
		if(mappingtype.equals("ensembl")) {
			mappingMap = Parsers.parseEnsembl(mapping);
		}else if(mappingtype.equals("go")){
			mappingMap = Parsers.parseGaf(mapping);
		}
		
		//Overlap file if needed
		if(!overlapout.equals("")) {
			System.out.println("Overlap file written into: " +overlapout);
			//writeOverlapFile(overlapout,dag);
		}
		
		//parse enrich file
		Pair<HashMap<String, Pair<Double, Boolean>>,List<String>> enrichOutputPair = Parsers.parseEnrich(enrich);
		enrichMap= enrichOutputPair.first;
		eIds=enrichOutputPair.second;
		
		
		HashMap<String,Set<String>> mapTest = Utilities.getGOTerm2Gene(mappingMap,id2node);
		for(String key :mapTest.keySet() ) {
			if(key.equals("GO:0006612")|| key.equals("GO:0032271")) {
			System.out.print(key + "\t");
			System.out.print(mapTest.get(key).size()+"\t");
			String prefix = "";
			for(String value : mapTest.get(key)) {
				System.out.print(prefix + value );
				prefix=",";
			}
			System.out.println();
			}
		}
		
		//Find overlap 
		for(String key :mapTest.keySet() ) {
			//Filter size! in intervall [minsize,maxsize]
			if(mapTest.get(key).size() >=minsize && mapTest.get(key).size()<=maxsize) {
				
				for(String key2 : mapTest.keySet()) {
					if(mapTest.get(key2).size() >=minsize && mapTest.get(key2).size()<=maxsize) {
						
						
						Set<String> intersection = new HashSet<String>(mapTest.get(key)); // use the copy constructor
						intersection.retainAll(mapTest.get(key2));
						
						if(key.equals("GO:0006612") || key2.equals("GO:0006612")){
							if(key.equals("GO:0032271") || key2.equals("GO:0032271")){
								
							if(intersection.size()>0) {
							System.out.print(key +"\t");
							System.out.print(key2+"\t");
							System.out.println(intersection.size());
							}
							}
						
//						for(String s : intersection ) {
//							System.out.println(s);
//						}
						}
						
					}
					
				}
				
			}	
		}
		
		

		
	}
	
	
	public static void writeOverlapFile(String filename , HashMap<String,List<String>> map) {
		
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			dos.print("term1\tterm2\tis_relative\tpath_length\tnum_overlapping\tmax_ov_percentage\n");
			
			for(String id : map.keySet()) {
				//map.
				
			}
			
			
			
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	public static void readCommandLine(String[] args) {

		try {
			Options options = new Options();

			options.addOption("obo", true, "obo file");
			options.addOption("o", true, "output file path");
			options.addOption("root", true, "GO namespace");
			options.addOption("mapping", true, "gene2go_mapping");
			options.addOption("mappingtype", true, "ensembl|go");
			options.addOption("enrich", true, "simulation file");
			options.addOption("minsize", true, "integer - minsize");
			options.addOption("maxsize", true, "integer- maxsize");
			options.addOption("overlapout", true, "overlap_out_tsv - optional");
			

			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			// TODO check if all param!
			if ( cmd.hasOption("obo") && cmd.hasOption("root") && cmd.hasOption("mapping") && cmd.hasOption("mappingtype")
					&& cmd.hasOption("enrich") && cmd.hasOption("o") && cmd.hasOption("maxsize") && cmd.hasOption("minsize")){

				
				obo=cmd.getOptionValue("obo");
				root=cmd.getOptionValue("root");
				mapping=cmd.getOptionValue("mapping");
				mappingtype=cmd.getOptionValue("mappingtype");
				o=cmd.getOptionValue("o");
				minsize=Integer.parseInt(cmd.getOptionValue("minsize"));
				maxsize= Integer.parseInt(cmd.getOptionValue("maxsize"));
				enrich=cmd.getOptionValue("enrich");
				mapping=cmd.getOptionValue("mapping");
				if(cmd.hasOption("overlapout")) {
					
					overlapout = cmd.getOptionValue("overlapout");
					
				}
				 
			}
			else{
				System.out.println();
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "ant", options );
				System.exit(0);

			}

	} catch (ParseException e) {
		System.err.println("Error Reading the command line parameters!");
		e.printStackTrace();
	}
	}
}
