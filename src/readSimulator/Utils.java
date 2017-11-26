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

		//GTF file to parse
		String gtf= "/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.gtf";
		exonSkipping.parserGTF.parse(gtf);

		String gene = "ENSG00000183091";
		String transcript = "ENST00000427231";
//
//		RegionVector rv = getGenomicRV(  1666,1741,gene, transcript,annotation);
//		System.out.println(Utils.prettyRegionVector(rv));


		RegionVector rv1 = getGenomicRV( 175,250,gene, transcript,annotation);
		System.out.println(Utils.prettyRegionVector(rv1));

		RegionVector rv2 = getGenomicRV( 2025,2100,"ENSG00000162946","ENST00000439617",annotation);
		System.out.println(Utils.prettyRegionVector(rv2));




	}

	public static RegionVector getGenomicRV(int start, int stop, String geneID, String transcriptID, Annotation annotation){

		RegionVector rv = new RegionVector();
		Gene gene = annotation.getGenes().get(geneID);
		Transcript transcript = gene.getTranscripts().get(transcriptID);
		String strand = gene.getStrand();
		System.out.println("Strand "+ strand);

		//Consider strandness
		if(strand.equals("-")){
			int temp = start;
			start= transcript.getRegionVectorExons().getRegionsLength()-stop;
			stop =transcript.getRegionVectorExons().getRegionsLength()-temp;
		}

		System.out.println("STOP: "+ stop);

		//calculate genomic positions
		int Gstart = getGenomicPosition(start,transcript);
		int Gstop = getGenomicPosition(stop,transcript);




		System.out.println("GSTART "+Gstart);
		System.out.println("GSTOP "+Gstop);

		//Get intersections
		Region genomicRegion = new Region(Gstart, Gstop);
	    Collections.sort(transcript.getRegionVectorExons().getVector());
		rv = transcript.getRegionVectorExons().getIntersect(genomicRegion);

		System.out.println(Utils.prettyRegionVector(transcript.getRegionVectorExons()));


		if(rv.getVector().size()==0){
			rv.getVector().add(new Region(Gstart,Gstop));
		}

		return rv;
	}


	private static int getGenomicPosition(int i, Transcript transcript) {
		Vector<Integer> transcriptPositions =getPositionVector(transcript);
	    Collections.sort(transcriptPositions);
	    Collections.sort(transcript.getRegionVectorExons().getVector());



//	    System.out.println("GET genomic position debug");
//	    System.out.println("input position: "+i);
	    int position = transcript.getRegionVectorExons().getRegionsLength()-i;

//	    //consider strandness
//		String geneID = transcript.getGeneId();
//		String strand =  Utils.annotation.getGenes().get(geneID).getStrand();
//		if(strand.equals("+")){
//			position=i;
//		}

	    position=i;
//	    System.out.println("input position converted: "+position);
		//calculate index
	    int index = Collections.binarySearch(transcriptPositions,position);

	    if(index<0){
	    	index++;
	    	index++;
	    	index=Math.abs(index);
	    }




        int supplement = position -transcriptPositions.get(index);


//        System.out.println("index "+index);
//        System.out.println("Supplement "+supplement);
//        System.out.println("Position "+position);
//        System.out.println("Transcript position index "+transcriptPositions.get(index));
//        System.out.println(transcriptPositions);
//        System.out.println(Utils.prettyRegionVector(transcript.getRegionVectorExons()));
		return transcript.getRegionVectorExons().getElement(index).getStart() + supplement;
	}



	public static Vector<Integer> getPositionVector(Transcript transcript){

		String geneID = transcript.getGeneId();
		String strand =  Utils.annotation.getGenes().get(geneID).getStrand();
	    Collections.sort(transcript.getRegionVectorExons().getVector());

		Vector<Integer> v = new Vector<Integer>();
		int cumLength = 0 ;
		if(strand.equals("-l")){
			Vector<Region> exons = transcript.getRegionVectorExons().getVector();

			for (int i = exons.size() - 1 ; i >= 0 ; i--) {
				v.add(cumLength);
				cumLength+=exons.get(i).getLength();

			}


		}else{


			for(Region r :transcript.getRegionVectorExons().getVector()){
				v.add(cumLength);
				cumLength+=r.getLength();
			}
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

		StringBuilder sb = new StringBuilder(sequence.length());

		int limit = sequence.length();
		String a = "";

		for(int i = 0 ; i<limit; i++){



			int j = 1 ;
			switch(sequence.charAt(i)){

				case 'A':
					a="T"+a;
					sb.append('T');
					j = 0;
					break;
				case 'C':
					a="G"+a;
					sb.append('G');
					j = 0;
					break;
				case 'T':
					a="A"+a;
					sb.append('A');
					j = 0;
					break;
				case 'G':
					a="C"+a;
					sb.append('C');
					j = 0;
					break;
				case 'N':
					a="N"+a;
					sb.append('N');
					j = 0;
					break;
			}
			if(j==1){
				System.out.println("position where it goes wrong "+i);
				System.out.println(sequence.charAt(i));


			}

		}


		String string = sb.reverse().toString();
		if(a.length() != sequence.length()){
			System.out.println("Problem with utils.getRevComplement function");
			System.out.println("input length "+sequence.length());
			System.out.println("output length "+ string.length());
			System.out.println(sequence);
			System.out.println(string);
		}
		return a;
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
