package exonSkipping;

import java.util.Vector;

public class RegionVector {

	private String geneId;
	private Vector<Region> vector;






	public RegionVector() {
		super();
		// TODO Auto-generated constructor stub
	}





	public RegionVector(String geneId, Vector<Region> vector) {
		super();
		this.geneId = geneId;
		this.vector = vector;

	}





	public Vector<Region> inverse(){

		Vector<Region> reverse = new Vector<Region>();
		int x1 = Runner.annotation.getGeneById(this.geneId).getStart();
		int x3= Runner.annotation.getGeneById(this.geneId).getStart();
		Region intron;

		//angenommen die sind sortiert
		for (Region r : vector) {

			//nop exon at beginning
			int x2 = r.getStart();

			if(x2>x1){
				intron = new Region(x1,x2);
				x1= r.getEnd();
				reverse.add(intron);
			}else{

				x1= r.getEnd();
			}
			//exon at beginning



		}

		intron = new Region(x1,x3);
		reverse.add(intron);



		return null;
	}



	public String getGeneId() {
		return geneId;
	}


	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}


	public Vector<Region> getVector() {
		return vector;
	}


	public void setVector(Vector<Region> vector) {
		this.vector = vector;
	}






}
