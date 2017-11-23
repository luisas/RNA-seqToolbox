package readSimulator;

import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import plots.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;



public class Utils {


	public static Annotation annotation=new Annotation();

	public static void main(String[] args){

		System.out.println("Class Utils run");
		//GTF file to parse
		String gtf= "/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.gtf";
		exonSkipping.parserGTF.parse(gtf);
		System.out.println("parsed!");

		String gene = "ENSG00000160767";
		String transcript = "ENST00000361361";

		RegionVector rv = getGenomicRV( 491,566,gene, transcript,annotation);

		//System.out.println("POS "+getGenomicPosition(810, annotation.getGenes().get(gene).getTranscripts().get(transcript)) + " Statt 50017390");
		//System.out.println("----------");
		System.out.println(Utils.prettyRegionVector(rv));
//		System.out.println("---------------------------------------------_LLL----------------------");
//		RegionVector rv1 = getGenomicRV( 192,267,gene, transcript,annotation);
//		System.out.println(Utils.prettyRegionVector(rv1));
//		System.out.println("---------------------------------------------_LLL----------------------");
//		RegionVector rv2 = getGenomicRV( 145,220,gene, transcript,annotation);
//		System.out.println(Utils.prettyRegionVector(rv2));

	}

	public static RegionVector getGenomicRV(int start, int stop, String geneID, String transcriptID, Annotation annotation){

		RegionVector rv = new RegionVector();

		Gene gene = annotation.getGenes().get(geneID);
		Transcript transcript = gene.getTranscripts().get(transcriptID);

		int Gstart = getGenomicPosition(start,transcript);
		//System.out.println("Genomic Start : "+Gstart);

		int Gstop = getGenomicPosition(stop,transcript);
		//System.out.println("Genomic Stop : "+Gstop);

		Region genomicRegion = new Region(Gstart, Gstop);

		//System.out.println("Genomic region vector: ");
		rv = transcript.getRegionVectorExons().getIntersect(genomicRegion);

		if(rv.getVector().size()==0){
			rv.getVector().add(new Region(Gstart,Gstop)); 
		}

		return rv;
	}


	private static int getGenomicPosition(int i, Transcript transcript) {
		Vector<Integer> transcriptPositions =getPositionVector(transcript);
	    Collections.sort(transcriptPositions);
	    // Get the value smaller than the value u were looking for
	    int index = Collections.binarySearch(transcriptPositions, i);
	    if(index<0){
	    	index++;
	    	index++;
	    	index=Math.abs(index);
	    }
	    //IF EXACTLY FOUND????
        int supplement = i -transcriptPositions.get(index);

		return transcript.getRegionVectorExons().getElement(index).getStart() + supplement;
	}



	public static Vector<Integer> getPositionVector(Transcript transcript){

		Vector<Integer> v = new Vector<Integer>();
		int cumLength = 0 ;
		for(Region r :transcript.getRegionVectorExons().getVector()){
			v.add(cumLength);
			cumLength+=r.getLength();
		}

		return v;

	}



	/**
	 * Other utils
	 * @param filename
	 * @return
	 */
	public static HashMap<String,HashMap<String, Integer>> parseReadCount(String filename){


		HashMap<String,HashMap<String, Integer>> genes = new HashMap<String,HashMap<String, Integer>>();

		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;

			while ((currentLine = br.readLine()) != null ) {



				if(!currentLine.startsWith("gene")) {
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);

					int i = 0 ;
					String key = "";
					String t="";
			        while (defaultTokenizer.hasMoreTokens())
			        {
			        	switch(i){
			        		case 0:
			        			key= defaultTokenizer.nextToken();

			        		case 1:
			        			t=defaultTokenizer.nextToken();

			        		case 2:

			        			int c = Integer.parseInt(defaultTokenizer.nextToken());

			        			if(genes.containsKey(key)){
			        				genes.get(key).put(t,c);
			        			}else{
			        				HashMap<String, Integer> tandc = new HashMap<String, Integer>();
				        			tandc.put(t,c);
			        				genes.put(key, tandc);
			        			}

			        	}
			        	i++;
			        }
			}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return genes;

	}

	public static String getComplement(String sequence){

		StringBuilder sb = new StringBuilder();

		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.append("T");
				case 'C':
					sb.append("G");
				case 'T':
					sb.append("A");
				case 'G':
					sb.append("C");
			}
		}

		return sb.toString();
	}




	public static String getRevComplement(String sequence){

		StringBuilder sb = new StringBuilder();


		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.insert(0,"T");
					break;
				case 'C':
					sb.insert(0,"G");
					break;
				case 'T':
					sb.insert(0,"A");
					break;
				case 'G':
					sb.insert(0,"C");
					break;
				case 'N':
					sb.insert(0, "N");
					break;
			}
		}

		return sb.toString();
	}

	public static String prettyMutations(TreeSet<Integer> set){
		//Collections.sort(set);
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (Integer mut: set){
			if(mut==-1){
				sb.append(prefix+"-");
				continue;
			}
			sb.append(prefix+mut);
			prefix=",";
		}
		//System.out.println("MUT "+sb.toString());
		return sb.toString();

	}


	public static int getRandomInbetween(int x, int y){
		return ThreadLocalRandom.current().nextInt(y-x)+x;

	}
	public static int getRandomPos(int transcriptLength, int FL) {

		return ThreadLocalRandom.current().nextInt(0, (transcriptLength-FL) + 1);

	}



	public static String prettyRegionVector(RegionVector rv) {

		StringBuilder sb = new StringBuilder();
		String prefix="";
		for(Region r : rv.getVector()) {
			sb.append(prefix+r.getStart()+"-"+r.getEnd());
			prefix="|";
		}
		return sb.toString();
	}





}
