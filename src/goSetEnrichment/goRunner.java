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
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
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
	
	static List<String> results1 = new ArrayList<String>();
	static List<String> results2 = new ArrayList<String>();
	static List<String> results3 = new ArrayList<String>();
	static List<List<String>> results4 = new ArrayList<List<String>>();
	
	
	static double[] pValues_hg_fdr ;
	static double[] pValues_fej_fdr;
	static double[] pValues_ks_fdr;
	
	static List<Integer> indexes_hg = new ArrayList<Integer>(); 
	static List<Integer> indexes_fej= new ArrayList<Integer>(); 
	static List<Integer> indexes_ks= new ArrayList<Integer>(); 
	
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
//		for(Node p : id2node.get("GO:0043433").parents) {
//			System.out.println(p.id);
//		}
//		for(Node node: id2node.values()) {
//			if(node.name.equals("regulation of nucleotide biosynthetic process")) {
//				System.out.println(node.id);
//			}
//		}	GO:0044283

		
		HashMap<String,Set<String>> go2gene = Utilities.getGOTerm2Gene(mappingMap,id2node);		
		
		//Overlap file if needed
		if(!overlapout.equals("")) {
			writeOverlapFile(overlapout,go2gene);
		}
		
		//parse enrich file
		Pair<HashMap<String, Pair<Double, Boolean>>,List<String>> enrichOutputPair = Parsers.parseEnrich(enrich);
		enrichMap= enrichOutputPair.first;
		eIds=enrichOutputPair.second;


		//Final file 
		writeOutputFile(o,go2gene);
		
		printOut(o);
		System.out.println("Ready!");
		
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//// NORMAL OUTPUT FILE!
/////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	public static void writeOutputFile(String filename, HashMap<String,Set<String>> go2gene) {

		//POPULATION SIZE --> N

		Set<String> popInt = new HashSet<String>(); 
		for(String go : go2gene.keySet()) {
			if(id2node.keySet().contains(go)) {
				popInt.addAll(go2gene.get(go));
			}
		}
		popInt.retainAll(enrichMap.keySet());
		int populationSize= popInt.size();
		
		//DEGs --> K
		Set<String> eDegs = new HashSet<String>();
		
		for(String gene : popInt) {
			if(enrichMap.get(gene).second) {
				eDegs.add(gene);
			}
		}
		
		int degs = eDegs.size();
	
		 
		 List<Double> pHG = new ArrayList<Double>();
		 List<Double> pFEJ = new ArrayList<Double>();
		 List<Double> pKS = new ArrayList<Double>();


		
		for(String key :go2gene.keySet() ) {
			
			if(go2gene.get(key).size() >=minsize && go2gene.get(key).size()<=maxsize && id2node.containsKey(key)) {
				

				
				
		////////////////////////////////////////////////////////////////////////////		
		////////////////////////////////////////////////////////////////////////////		
		///////////					REAL OUTPUT
		//////////////////////////////////////////////////////////////////////////		
		////////////////////////////////////////////////////////////////////////////		
				
					
				StringBuilder sb = new StringBuilder();
					
				//ID
				sb.append(key+"\t");
				
				//NAME
				sb.append(id2node.get(key).name+"\t");
				
				//SIZE
				Set<String> intersection = new HashSet<String>(go2gene.get(key)); 
				intersection.retainAll(enrichMap.keySet());
				int setSize = intersection.size();
				sb.append(intersection.size()+"\t");
				
				boolean flag = false;
				//is_true
				if(eIds.contains(key)) {
					flag=true;
					sb.append("true\t");
				}else {
					sb.append("false\t");
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
				sb.append(noverlap+"\t");
				
				/////////////////////////////////////////////////
				///----------------------STATISTICS
				////////////////////////////////////////////////
				
				//hg_pval
				HypergeometricDistribution hg = new HypergeometricDistribution( populationSize,  degs,  setSize);
				double hg_pval = hg.upperCumulativeProbability(noverlap);
				
				pHG.add(hg_pval);
				
				sb.append(hg_pval+"\t");
				
				//--------------------------ADD TO RESULTS
				
				results1.add(sb.toString());
				sb = new StringBuilder(); 
//---------------------------------------------------------------------------------------FIN RES 1--> hg fdr					

				//fej_pval : CHANGE
				HypergeometricDistribution fej = new HypergeometricDistribution( populationSize-1,  degs-1,  setSize-1);
				double fej_pval = fej.upperCumulativeProbability(noverlap-1);
				sb.append(fej_pval+"\t");
				pFEJ.add(fej_pval);
				results2.add(sb.toString());
				sb = new StringBuilder(); 
//---------------------------------------------------------------------------------------FIN RES 2--> fej fdr					
				
				//KS			
				KolmogorovSmirnovTest ks = new KolmogorovSmirnovTest();
				double[] in_set_distrib = new double[intersection.size()];
				//GENES IN SET 
				int i = 0 ; 
				for(String gene : intersection) {
					in_set_distrib[i]=enrichMap.get(gene).first;
					i++;
				}
				//GENES in REST OF POPULATION 
				Set<String> rest = new HashSet<String>(popInt);
				rest.removeAll(intersection);
				double[] bg_distrib=new double[rest.size()];
				i=0;
				for(String gene : rest) {
					bg_distrib[i]=enrichMap.get(gene).first;
					i++;
				}
				//ks stat
				sb.append(ks.kolmogorovSmirnovStatistic(in_set_distrib, bg_distrib)+"\t");
				//kspval 
				double ks_pval =ks.kolmogorovSmirnovTest(in_set_distrib, bg_distrib);
				sb.append(ks_pval+"\t");
				
				pKS.add(ks_pval);

//---------------------------------------res 3 --> ks fdr
				results3.add(sb.toString());
				
				
				/////////////////////////////////////////////////
				///----------------------MIN PATH TO TRUE
				////////////////////////////////////////////////
				sb=new StringBuilder();				
			
				List<String> eSet = new ArrayList<String>(); 	
				eSet.addAll(id2node.keySet());
				eSet.retainAll(eIds);
				

				if(!flag && !eSet.isEmpty()) {
					int min = Integer.MAX_VALUE;
					String lca = "";
					String minTrue = ""; 

					int min1 = Integer.MAX_VALUE;
					int min2 = Integer.MAX_VALUE;
					
					for(String tr : eSet) {
			
						Pair<Pair<Integer,Integer>,String> sp = Utilities.get_sp(id2node.get(key),id2node.get(tr),id2node).first;	
						if(min>=(sp.first.first+sp.first.second-1)) {
								min = sp.first.first+sp.first.second-1;
								min1= sp.first.first;
								min2= sp.first.second;
								minTrue = tr; 
								lca=sp.second; 
						}

						
					}

						List<String> names = get_Names_to_true(key,lca,minTrue,min1, min2);

						results4.add(names);		
					
					}else {
						results4.add(null);
					}
				
				
			}
		}
		
		
		//here finished iteration: GET BENJAMINI HOCHBERG ADJUSTED P VALUES
		double[] pValues_hg = toArray(pHG);
	
		BenjaminiHochbergFDR BH = new BenjaminiHochbergFDR(pValues_hg);
		BH.calculate();
		System.out.println(BH.getIndex().length);
		pValues_hg_fdr = BH.getAdjustedPvalues();
		for(int j = 0 ; j < BH.getIndex().length; j++) {
			indexes_hg.add(BH.getIndex()[j]);
		}
		
		
		double[] pValues_fej = toArray(pFEJ);
		BH = new BenjaminiHochbergFDR(pValues_fej);
		BH.calculate();
		pValues_fej_fdr = BH.getAdjustedPvalues();
		for(int i = 0 ; i < BH.getIndex().length; i++) {
			indexes_fej.add(BH.getIndex()[i]);
		}
		
		
		double[] pValues_ks = toArray(pKS);
		BH = new BenjaminiHochbergFDR(pValues_ks);
		BH.calculate();
		pValues_ks_fdr = BH.getAdjustedPvalues();
		for(int i = 0 ; i < BH.getIndex().length; i++) {
			indexes_ks.add(BH.getIndex()[i]);
		}
		
	}

	
/////////////////////////////////////////////////////////////////////////////////////////
////PRINT FUNCTION 
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void printOut(String filename) {
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			dos.print("term\tname\tsize\tis_true\tnoverlap\thg_pval\thg_fdr\tfej_pval\tfej_fdr\tks_stat\tks_pval\tks_fdr\tshortest_path_to_a_true\n");

			//each line
			for(int i =0 ; i< results1.size(); i++) {
				dos.print(results1.get(i));
				dos.print(pValues_hg_fdr[indexes_hg.indexOf(i)]+"\t");
				dos.print(results2.get(i));
				dos.print(pValues_fej_fdr[indexes_fej.indexOf(i)]+"\t");
				dos.print(results3.get(i));
				dos.print(pValues_ks_fdr[indexes_ks.indexOf(i)]+"\t");
				
				String prefix = "";
				if(results4.get(i)!=null) {
				for(String name : results4.get(i)){
					dos.print(prefix+name);
					prefix="|";
				}
				}
				dos.println();
			}
			
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//// 	HELP FUNCTIONS
/////////////////////////////////////////////////////////////////////////////////////////	

	public static double[] toArray(List<Double> list) {
		double[] result = new double[list.size()];
		int x = 0 ;
		for(double d : list){
			result[x] = d;
			x++;
		}
		return result; 
	}

	public static List<String> get_Names_to_true(String key, String lca, String minTrue, Integer n, Integer n2){
		List<String> result = new ArrayList<String>();
		HashMap<String, List<String>> mapVal1 = Utilities.get_sp(id2node.get(key),id2node.get(lca),id2node).second.first;
		HashMap<String, List<String>> mapVal2 =Utilities.get_sp(id2node.get(minTrue),id2node.get(lca),id2node).second.first; 
		String x = lca; 		

		//-----------------------LEFT PATH
			List<String> list = new ArrayList<String>();
			list.add(lca);
			List<String> res = new ArrayList<String>();
			List<List<String>> results = new ArrayList<List<String>>();
			recursion(mapVal1,list , key, 0, n, res, results) ;
			List<String> official = new ArrayList<String>();
			for(List<String> pSP : results) {
				if(pSP.size() == n+1 ) {
					official = pSP;
				}
			}
			for(int i = 1 ; i<official.size()-1 ; i++) {
				String s = official.get(i);
				result.add(0,id2node.get(s).name);
			}
			if(n!=0) {
			result.add(0,id2node.get(key).name);
			}
		
		
		x = lca; 
		result.add(id2node.get(x).name+" *");
		//System.out.println("---------------------------------------------------");
		list = new ArrayList<String>();
		list.add(lca);
		res = new ArrayList<String>();
		results = new ArrayList<List<String>>();
		recursion(mapVal2,list , minTrue, 0, n2, res, results) ;
		 official = new ArrayList<String>();
		 //min = Integer.MAX_VALUE;
		for(List<String> pSP : results) {
			//if(min>= pSP.size()) {
				//min = pSP.size();
			if(pSP.size() == n2+1 ) {
				official = pSP;
			}
			//}
		}
		for(int i = 1 ; i<official.size()-1 ; i++) {
			String s = official.get(i);
			result.add(id2node.get(s).name);
		}
		if(n2!=0) {
		result.add(id2node.get(minTrue).name);
		}
		return result;
		
	}
	

	
	//find all paths to lca 
	public static void recursion(HashMap<String, List<String>> mapVal1, List<String> list , String stop, Integer l , Integer length, List<String> res, List<List<String>> result) {
		
		for(String key : list) {
			if(mapVal1.containsKey(key)) {
//				System.out.println("key "+ key + " level "+l);

				if(res.size()>l) {
					res.remove(res.get(l));
				}
				res.add(l, key);
				List<String> next = new ArrayList<String>();
				next.addAll(mapVal1.get(key));
				
				if(l==length && res.get(l).equals(stop)) {
//					System.out.println("######");
					result.add(new ArrayList<String>());
					for(int i =0 ; i< l+1 ;i++ ) {
						result.get(result.size()-1).add(res.get(i));
					}
					return;
				}
				recursion( mapVal1,  next ,  stop, l+1, length, res , result);	

			}

		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
////					OVERLAP
/////////////////////////////////////////////////////////////////////////////////////////
	
	public static void writeOverlapFile(String filename , HashMap<String,Set<String>> go2gene) {
		
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			dos.print("term1\tterm2\tis_relative\tpath_length\tnum_overlapping\tmax_ov_percent\n");
			
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
						
							term1=key ; 
							term2 = key2; 
							
							if(key.equals(term1) || key2.equals(term1)){
								if(key.equals(term2) || key2.equals(term2)){
								
								if(intersection.size()>0) {
									float perc1 = (float)intersection.size()/(float)go2gene.get(term1).size(); 
									float perc2 = (float)intersection.size()/(float)go2gene.get(term2).size(); 
									float max = Float.max(perc1, perc2)*100;
									String maxWith2CommaValues = String.format ("%.2f", max);

									dos.print(key +"\t"+key2+"\t");
									dos.print(Utilities.is_relative(term1, term2, id2node)+"\t");
									Pair<Pair<Integer,Integer>,String> spcalc = Utilities.get_sp(id2node.get(term1), id2node.get(term2), id2node).first;
									int spath = spcalc.first.first + spcalc.first.second-1;
									if(spcalc.first.first==0 || spcalc.first.second==0) {
										spath++;
									}

									//int spath =Utilities.get_sp(node1, node2, id2node); 
									dos.print(spath + "\t");
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
	
	
/////////////////////////////////////////////////////////////////////////////////////////
//// COMMAND LINE PARAMS
/////////////////////////////////////////////////////////////////////////////////////////
	
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
