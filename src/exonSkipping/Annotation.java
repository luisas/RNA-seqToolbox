package exonSkipping;

import java.util.HashMap;
import java.util.Iterator;

public class Annotation {

	private HashMap<String,Gene > genes;
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, RegionVector> exons;
	private HashMap<String, RegionVector> cds;


	//Looks for a gene by its ID
	Gene getGeneById(String id) {

		return genes.get(id);
	}

	//Define if a gene has a CDS
	//Pair<Gene, RegionVector> getRegionVector(String id, boolean cds){


	//}

	Iterator<Gene> getGenes(String chr, int start, int end){


		return null;
	}



	int getNumberTranscripts(Gene gene) {

		return gene.getTranscriptIds().size();


	}


	int getNumberOfCds(Gene gene) {


		int number= gene.getCds().size();






		return number;

	}





	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public void setGenes(HashMap<String, Gene> genes) {
		this.genes = genes;
	}




	public HashMap<String, RegionVector> getExons() {
		return exons;
	}

	public void setExons(HashMap<String, RegionVector> exons) {
		this.exons = exons;
	}

	public HashMap<String, RegionVector> getCds() {
		return cds;
	}

	public void setCds(HashMap<String, RegionVector> cds) {
		this.cds = cds;
	}

	public HashMap<String, Transcript> getTranscripts() {
		return transcripts;
	}

	public void setTranscripts(HashMap<String, Transcript> transcripts) {
		this.transcripts = transcripts;
	}




}
