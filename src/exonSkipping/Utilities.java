package exonSkipping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class Utilities {





	public static void printGene(Gene gene) {

		System.out.print("GENE:    ");
		System.out.println(gene.getId() +"\t"+ gene.getName() +"\t"+gene.getStart()+ "\t"+gene.getStop());
		System.out.print( "\t" +"\t List of Transcripts");
		System.out.println(Collections.singletonList(gene.getTranscriptIds()));

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





}
