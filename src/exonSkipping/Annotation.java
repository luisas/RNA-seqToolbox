package exonSkipping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Annotation {

	private HashMap<String,Gene > genes;
	//private HashMap<String, RegionVector> transcripts;
	//private HashMap<String, RegionVector> exons;
	//private HashMap<String, RegionVector> cds;



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

		return 0;


	}


	int getNumberOfCds(Gene gene) {

		//RegionVector rv = new RegionVector("hola","joa", gene.getCds());

		//Vector<Region> v = rv.merge();
		//int number= v.size();
		//Utilities.printVector(v);

		return 0;

	}





	public HashMap<String, Gene> getGenes() {
		return genes;
	}

	public void setGenes(HashMap<String, Gene> genes) {
		this.genes = genes;
	}









}
