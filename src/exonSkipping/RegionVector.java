package exonSkipping;

import java.util.Collections;
import java.util.Comparator;
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


	Comparator<Region> comparator = new Comparator<Region>() {
	    @Override
	    public int compare(Region left, Region right) {
	        return left.getStart() - right.getStart(); // use your logic
	    }
	};

	 // use the comparator as much as u want



	public Vector<Region> inverse(){

		Vector<Region> reverse = new Vector<Region>();
		int x1 = Runner.annotation.getGeneById(this.geneId).getStart();
		int x3= Runner.annotation.getGeneById(this.geneId).getStart();
		Region intron;
		Collections.sort(vector, comparator);
		//System.out.println(Collections.singletonList(this.vector));
		Utilities.printVector(vector);
		//angenommen die sind sortiert
		for (Region r : this.vector) {

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



		return reverse;
	}


	public RegionVector substract(RegionVector first, RegionVector second){



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
