package goSetEnrichment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import org.apache.commons.math3.distribution.HypergeometricDistribution;

import differentialExpression.BenjaminiHochbergFDR;
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
	
	static HashMap<String, Node> id2node; 
	
	public static void main(String[] args ) {
		
		readCommandLine(args);
		
		
		Pair<DAG, HashMap<String, Node>> pairDag=Parsers.parseObo(obo,root);
		 id2node = pairDag.second;
		dag = pairDag.first;
		
		
		if(mappingtype.equals("ensembl")) {
			mappingMap = Parsers.parseEnsembl(mapping);
		}else if(mappingtype.equals("go")){
			mappingMap = Parsers.parseGaf(mapping);
		}
		
		HashMap<String,Set<String>> go2gene = Utilities.getGOTerm2Gene(mappingMap,id2node);
		//Overlap file if needed
		if(!overlapout.equals("")) {
			//System.out.println("Overlap file written into: " +overlapout);
			
			//writeOverlapFile(overlapout,go2gene);
		}
		
		//parse enrich file
		Pair<HashMap<String, Pair<Double, Boolean>>,List<String>> enrichOutputPair = Parsers.parseEnrich(enrich);
		enrichMap= enrichOutputPair.first;
		eIds=enrichOutputPair.second;
		
		//Final file 
		writeOutputFile(o,go2gene);
		
	}
	
	public static void writeOutputFile(String filename, HashMap<String,Set<String>> go2gene) {
		FileWriter fos;
		int populationSize = enrichMap.size();
		int p2 = go2gene.size();
		Set<String> genes = new HashSet<String>();
		for (Set<String> set : go2gene.values()) {
			for(String s: set) {
				genes.add(s);
			}
		}
		System.out.println("genes size "+genes.size());
		
		Set<String> interse = new HashSet<String>(genes); 
		interse.retainAll(enrichMap.keySet());
		System.out.println("interse "+interse.size());
		int degs =0;
		
		for(Pair<Double, Boolean> pair :enrichMap.values()) {
			if(pair.second) {
				degs++;
			}
		}
		System.out.println("Population size: "+populationSize);
		System.out.println("degs: "+degs);
		

		
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			dos.print("term\tname\tsize\tis_true\tnoverlap\thg_pval\thg_fdr\tfej_pval\tfej_fdr\tks_stat\tks_pval\tks_fdr\tshortest_path_to_a_true\n");

			
			for(String key :go2gene.keySet() ) {
				
				if(go2gene.get(key).size() >=minsize && go2gene.get(key).size()<=maxsize && id2node.containsKey(key)) {
					if(key.equals("GO:0051046")) {
					//ID
					System.out.print(key+"\t");
					//NAME
					System.out.print(id2node.get(key).name+"\t");
					//SIZE
					Set<String> intersection = new HashSet<String>(go2gene.get(key)); 
					intersection.retainAll(enrichMap.keySet());
					int setSize = intersection.size();
					System.out.print(intersection.size()+"\t");
					
					//is_true
					if(eIds.contains(key)) {
						System.out.print("true\t");
					}else {
						System.out.print("false\t");
					}
					//signif
					int noverlap = 0 ; 
					for(String gene : go2gene.get(key) ) {
						if(enrichMap.containsKey(gene)) {
						if(enrichMap.get(gene).second) {
							noverlap++;
						}
						}
					}
					System.out.print(noverlap+"\t");
					
					///----------------------STATISTICS
					
					
					//hg_pval
					HypergeometricDistribution hg = new HypergeometricDistribution( interse.size(),  degs,  setSize);
					double hg_pval = hg.upperCumulativeProbability(noverlap);
					System.out.print(hg_pval+"\t");
					
					//hg_fdr
					double[] parray = {hg_pval};
					BenjaminiHochbergFDR BH = new BenjaminiHochbergFDR(parray);
					BH.calculate();
					double[] hg_fdr = BH.getAdjustedPvalues();
					System.out.print(hg_fdr[0]+"\t");
					
					System.out.println();
					}
				}
			}
			
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void writeOverlapFile(String filename , HashMap<String,Set<String>> go2gene) {
		
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			dos.print("term1\tterm2\tis_relative\tpath_length\tnum_overlapping\tmax_ov_percentage\n");
			
			//Find overlap 
			List<String> listKeys = new ArrayList<String>(go2gene.keySet()); 
			//for(String key :go2gene.keySet() ) {
			for(int index = 0 ; index < listKeys.size() ; index ++) {
				String key = listKeys.get(index);
				//Filter size! in intervall [minsize,maxsize]
				if(go2gene.get(key).size() >=minsize && go2gene.get(key).size()<=maxsize && id2node.containsKey(key)) {
					
					
					
					//for(String key2 : go2gene.keySet()) {
					for(int index2 = index+1 ; index2 < listKeys.size() ; index2 ++) {
						String key2 = listKeys.get(index2);
						if(!key2.equals(key) && go2gene.get(key2).size() >=minsize && go2gene.get(key2).size()<=maxsize && id2node.containsKey(key2)) {
							
							
							Set<String> intersection = new HashSet<String>(go2gene.get(key)); // use the copy constructor
							intersection.retainAll(go2gene.get(key2));
							String term1;
							String term2;
							if(test) {
							 term1="GO:0006612" ; 
							term2 = "GO:0048259"; 
							}else {
								term1=key ; 
								term2 = key2; 
							}
							if(key.equals(term1) || key2.equals(term1)){
								if(key.equals(term2) || key2.equals(term2)){
									
								if(intersection.size()>0) {
							
									//System.out.print(key +"\t");
//									System.out.print(key2+"\t");
//									System.out.print(Utilities.is_relative(term1, term2, id2node)+"\t");
//									System.out.print(Utilities.path_length(term1, term2, id2node) + "\t");
//									System.out.print(intersection.size() +"\t");
									float perc1 = (float)intersection.size()/(float)go2gene.get(term1).size(); 
									float perc2 = (float)intersection.size()/(float)go2gene.get(term2).size(); 
									float max = Float.max(perc1, perc2)*100;
									String maxWith2CommaValues = String.format ("%.2f", max);
//									System.out.print(maxWith2CommaValues.replaceAll(",", ".")+"\t");
//									System.out.println();
									
									
									dos.print(key +"\t"+key2+"\t");
									dos.print(Utilities.is_relative(term1, term2, id2node)+"\t");
									dos.print(Utilities.path_length(term1, term2, id2node) + "\t");
									dos.print(intersection.size() +"\t");
									dos.println(maxWith2CommaValues.replaceAll(",", ".")+"\t");
								}
								}
							

							}
							
						}
						
					}
					
				}	
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
