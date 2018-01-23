package exonSkipping;

import java.util.HashMap;
import java.util.Iterator;

public class Annotation {

	private HashMap<String,Gene > genes;

	public Annotation(){

		this.genes = new HashMap<String,Gene>()
;
	}
	public Annotation(HashMap<String, Gene> genes) {
		super();
		this.genes = genes;
	}


	public Gene getGeneById(String id) {

		return genes.get(id);
	}


	Iterator<Gene> getGenes(String chr, int start, int end){


		return null;
	}


	int getNumberTranscripts(Gene gene) {

		return 0;


	}

	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public void setGenes(HashMap<String, Gene> genes) {
		this.genes = genes;
	}









}
