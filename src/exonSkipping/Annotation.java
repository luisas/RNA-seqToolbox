package exonSkipping;

import java.util.HashMap;
import java.util.Iterator;

public class Annotation {
	
	private HashMap<String,Gene > genes;
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, RegionVector> regionvectors; 
	
	
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

	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public void setGenes(HashMap<String, Gene> genes) {
		this.genes = genes;
	}


	public HashMap<String, RegionVector> getRegionvectors() {
		return regionvectors;
	}

	public void setRegionvectors(HashMap<String, RegionVector> regionvectors) {
		this.regionvectors = regionvectors;
	}

	public HashMap<String, Transcript> getTranscripts() {
		return transcripts;
	}

	public void setTranscripts(HashMap<String, Transcript> transcripts) {
		this.transcripts = transcripts;
	}
	
	
	

}
