package exonSkipping;

import java.util.List;

public class Chromosome {
	
	private String id; 
	private List<Gene> genes; 
	
	public Chromosome(String id, List<Gene> genes){
		
		this.id = id; 
		this.genes= genes; 
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}
	

}
