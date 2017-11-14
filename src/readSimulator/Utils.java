package readSimulator;

import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;


public class Utils {






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
		Transcript transcript = gene.getTranscripts().get(transcriptID);

		RegionVector transcripts = gene.getRegionVectorTranscripts();
		RegionVector introns = transcripts.inverse();


		int count = 0 ;
		int countTranscript = 0 ;
		int limit = position ;
		int tstart= 0;
		int tend = 0;

	//	RegionVector previousTranscript = new RegionVector();

		for(Region region: introns.getVector()){


			tend=region.getStart();
			int lengthRegion = tend-tstart+1;
			countTranscript += lengthRegion;
			//previousTranscript.getVector().add(new Region(tstart,tend));

			while(countTranscript<limit){
				count+= region.getLength();
			}

			tstart=region.getEnd();

		}

		return count;

	}

	public static RegionVector getIntronsInbetween(int start, int stop, String geneID, String transcriptID, Annotation annotation){

		Gene gene = annotation.getGenes().get(geneID);

		RegionVector transcripts = gene.getRegionVectorTranscripts();
		RegionVector introns = transcripts.inverse();


		RegionVector inbetweenIntrons= new RegionVector();

		for(Region r : introns.getVector()){

			if(r.getStart()>=start && r.getEnd()<=stop ){
				inbetweenIntrons.getVector().add(r);
			}

		}

		return inbetweenIntrons;



	}

	public static RegionVector getGenomicPositions(int start, int stop, String geneID, String transcriptID, Annotation annotation){

		RegionVector rv = new RegionVector();

		Gene gene = annotation.getGenes().get(geneID);
		Transcript transcript = gene.getTranscripts().get(transcriptID);

		RegionVector transcripts = gene.getRegionVectorTranscripts();

		int Gstart = start + getIntronBasesBeforePosition(start, geneID, transcriptID, annotation);

		//System.out.println("GSTARt" + Gstart);

		int Gstop = stop + getIntronBasesBeforePosition(stop, geneID, transcriptID, annotation);
		//System.out.println("GSTopt" + Gstop);

		//GET introns inbetween
		RegionVector inbetweenIntrons = getIntronsInbetween(Gstart, Gstop, geneID, transcriptID, annotation);
		//RetrieveExons

		System.out.println("Number regions" + inbetweenIntrons.getNumberRegion());
		if(inbetweenIntrons.getNumberRegion() == 0){
			rv.getVector().add(new Region(Gstart,Gstop));
			return rv;
		}
		rv.getVector().add(new Region(Gstart,inbetweenIntrons.getFirst().getStart()));

		RegionVector allButStartEndTranscripts = inbetweenIntrons.inverse();

		rv.getVector().add(new Region(inbetweenIntrons.getLast().getEnd(),Gstop));

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


	public static String prettyMutations(Set<Integer> set){

		//Collections.sort(set);

		return "a";



	}

	public static String getRevComplement(String sequence){

		StringBuilder sb = new StringBuilder();


		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.insert(0,"C");
				case 'C':
					sb.insert(0,"A");
				case 'T':
					sb.insert(0,"G");
				case 'G':
					sb.insert(0,"T");
			}
		}

		return sb.toString();
	}

}
