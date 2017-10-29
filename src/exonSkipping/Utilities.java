package exonSkipping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class Utilities {





	public static void printGene(Gene gene) {

		System.out.print("GENE:    ");
		System.out.println(gene.getId() +"\t"+ gene.getName() +"\t"+gene.getStart()+ "\t"+gene.getStop());
		System.out.println( "\t" +"\t List of Transcripts");
		//System.out.println(Collections.singletonList(gene.getTranscriptIds()));
		printTranscripts(gene);
		//printRegionVector(gene.getRegionVectorTranscripts());
		
		//System.out.print( "\t" +"\t List of CDS");
		//printVector(gene.getCds());


	}





	public static void printTranscripts(Gene gene){
		for(String key: gene.getTranscripts().keySet()){
			printTranscript(gene.getTranscripts().get(key));

		}


	}
	public static void printTranscript(Transcript transcript){
		System.out.println("\t" +"\t"+transcript.getId()+"\t"+transcript.getStart()+ "\t"+transcript.getStop());
		//Utilities.printRegionVector(transcript.getRegionVectorCds()); 
		

	}
	public static void printGenes(HashMap<String, Gene> genes) {

		for (String key : genes.keySet()) {
			printGene(genes.get(key));
		}
	}


	public static void printVector(Vector<Region> vector){

		for(Region r: vector ){

			System.out.println(r.getStart()+":"+r.getEnd()+"----"+r.getLength());

		}

	}

	public static void printRegionVector(RegionVector rv) {


		for(Region r : rv.getVector()) {


			System.out.println("\t" +"\t"+r.getStart()+":"+r.getEnd()+"----"+r.getLength());

		}

	}


	public static void printCds(HashMap<String, RegionVector> map) {

		for(String key : map.keySet()) {
			printVector(map.get(key).getVector());

			System.out.println("hola");

		}


	}





}
