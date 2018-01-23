package goSetEnrichment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

	public static void main(String[] args ) {
		
		readCommandLine(args);
		
		//Parsers.parseObo(obo);
		
		if(mappingtype.equals("ensembl")) {	
			//Parsers.parseEnsembl(mapping);
		}else if(mappingtype.equals("go")){
			//Parsers.parseGaf(mapping);
		}
		Parsers.parseEnrich(enrich);
		
		
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
			options.addOption("overlapout", false, "overlap_out_tsv - optional");
			

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
