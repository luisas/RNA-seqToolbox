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
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;



public class Utils {


	public static Annotation annotation=new Annotation();
;

//	public static void main(String[] args){
//
//		System.out.println("Class Utils run");
//		//GTF file to parse
//		String gtf= "/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.gtf";
//		exonSkipping.parserGTF.parse(gtf);
//		System.out.println("parsed!");
//
//		String gene = "ENSG00000104870";
//		String transcript = "ENST00000599988";
//
//		RegionVector rv = getGenomicRV( 10060,10134,gene, transcript,annotation);
//
//		System.out.println(Utils.prettyRegionVector(rv));
//
//
//
//	}


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


	public double getFL(int readLength, double SD, double mean) {

		return 0;
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


	public static int getIntronBasesBeforePosition(int position, String geneID, String transcriptID, Annotation annotation){

		Gene gene = annotation.getGenes().get(geneID);
		RegionVector transcripts = gene.getTranscripts().get(transcriptID).getRegionVectorExons();
		System.out.println(Utils.prettyRegionVector(transcripts));


		RegionVector introns = transcripts.inverse();


		int count = 0 ;
		int countTranscript = 0 ;
		int tstart= transcripts.getFirst().getStart();
		System.out.println("posInGen: "+tstart);
		int limit = tstart+position ;

		int tend = 0;


	//	RegionVector previousTranscript = new RegionVector();

		System.out.println("INtrons");
		System.out.println(Utils.prettyRegionVector(introns));

		for(Region region: introns.getVector()){


			tend=region.getStart();
			int lengthRegion = tend-tstart+1;
			countTranscript += lengthRegion;
			System.out.println("count transcript"+countTranscript);
			System.out.println("count transcript"+limit);

			//previousTranscript.getVector().add(new Region(tstart,tend));

			while(countTranscript<limit){
				count+= region.getLength();
			}

			tstart=region.getEnd();

		}

		System.out.println("count"+ count);
		return count;

	}

//	public static RegionVector getIntronsInbetween(int start, int stop, Gene gene){
//
//
//		RegionVector exons = new RegionVector();
//
//		Transcript t ;
//		for(String key : gene.getTranscripts().keySet() ){
//			t = gene.getTranscripts().get(key);
//
//
//
//
//
//		}
//
//
//		//RegionVector introns = transcripts.inverse();
//
//
//		RegionVector inbetweenIntrons= new RegionVector();
//
////		for(Region r : introns.getVector()){
////
////			if(r.getStart()>=start && r.getEnd()<=stop ){
////				inbetweenIntrons.getVector().add(r);
////			}
////
////		}
//
//		return inbetweenIntrons;
//
//
//
//	}




	public static int getGenomicPosition(int tposition,Transcript  transcript){

		RegionVector exons =  transcript.getRegionVectorExons();
		int tstart = transcript.getStart();


		RegionVector exonBeforePos = new RegionVector();
		Region exon;


		Iterator<Region> it = exons.getVector().iterator();

		while(it.hasNext()){
			exon = it.next() ;
			exonBeforePos.getVector().add(exon);
			if(exonBeforePos.getRegionsLength()>tposition){
				break;
			}
		}


		RegionVector introns = exonBeforePos.inverse();
		int intronBases = introns.getRegionsLength();
		int gPosition= tposition+intronBases +tstart;

		return gPosition;



	}

	public static Pair<Integer, RegionVector> getGenomicPositionANDintronsRV(int tposition,Transcript  transcript){

		Pair<Integer,RegionVector> pair ;

		RegionVector exons =  transcript.getRegionVectorExons();
		int tstart = transcript.getStart();


		RegionVector exonBeforePos = new RegionVector();
		Region exon;


		Iterator<Region> it = exons.getVector().iterator();

		while(it.hasNext()){
			exon = it.next() ;
			exonBeforePos.getVector().add(exon);
			if(exonBeforePos.getRegionsLength()>tposition){
				break;
			}
		}


		RegionVector introns = exonBeforePos.inverse();
		int intronBases = introns.getRegionsLength();

		int gPosition= tposition+intronBases +tstart;


		pair = new Pair<Integer, RegionVector>( gPosition, introns);






		return pair;

	}




	public static RegionVector getGenomicRV(int start, int stop, String geneID, String transcriptID, Annotation annotation){

		System.out.println("-----------------------------------");
		RegionVector rv = new RegionVector();

		Gene gene = annotation.getGenes().get(geneID);
		Transcript transcript = gene.getTranscripts().get(transcriptID);

		RegionVector transcripts = gene.getRegionVectorTranscripts();

		System.out.println("Transcripts!: "+ Utils.prettyRegionVector(transcripts));

		int Gstart = getGenomicPosition(start,transcript);

		System.out.println("genomic start : "+Gstart);
		System.out.println("Tstart: "+start);
		//System.out.println("GSTARt" + Gstart);

		Pair<Integer,RegionVector> end_introns = getGenomicPositionANDintronsRV(stop, transcript);
		int Gstop = end_introns.first;
				//getGenomicPosition(stop,transcript);
		//System.out.println("GSTopt" + Gstop)
		System.out.println("genomic start : "+Gstop);
		System.out.println("tstop: "+ stop);

		//GET introns inbetween
		RegionVector inbetweenIntrons = end_introns.second;
		//RetrieveExons

		//System.out.println("Number regions" + inbetweenIntrons.getNumberRegion());
		if(inbetweenIntrons.getNumberRegion() == 0){
			rv.getVector().add(new Region(Gstart,Gstop));
			return rv;
		}
		rv.getVector().add(new Region(Gstart,inbetweenIntrons.getFirst().getStart()));

		rv.getVector().addAll(inbetweenIntrons.getVector());

		rv.getVector().add(new Region(inbetweenIntrons.getLast().getEnd(),Gstop));

		System.out.println("-----------------------------------");


		return rv;
	}


	public static String getComplement(String sequence){

		StringBuilder sb = new StringBuilder();

		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.append("C");
				case 'C':
					sb.append("A");
				case 'T':
					sb.append("G");
				case 'G':
					sb.append("T");
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

	public static String getRevComplement(String sequence){

		StringBuilder sb = new StringBuilder();


		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.insert(0,"C");
					break;
				case 'C':
					sb.insert(0,"A");
					break;
				case 'T':
					sb.insert(0,"G");
					break;
				case 'G':
					sb.insert(0,"T");
					break;
				case 'N':
					sb.insert(0, "N");
					break;
			}
		}

		return sb.toString();
	}




}
