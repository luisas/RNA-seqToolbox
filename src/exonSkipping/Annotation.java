package exonSkipping;

import java.util.HashMap;
import java.util.Iterator;

public class Annotation {
	
	private HashMap<String,Gene > genes;
	private HashMap<String, Chromosome> chromosomes;
	private HashMap<String, Transcript> transcripts;
	
	
	
	
	
	
	
	
	
	
	//Looks for a gene by its ID
	Gene getGeneById(String id) {
		
		return null;
	}
	
	//Define if a gene has a CDS
	//Pair<Gene, RegionVector> getRegionVector(String id, boolean cds){
		
		
	//}
	
	Iterator<Gene> getGenes(String chr, int start, int end){

		return null;
	}
	
	
	

}
