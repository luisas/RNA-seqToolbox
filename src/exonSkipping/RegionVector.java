package exonSkipping;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class RegionVector {

	private String id;
	private String geneId;
	private Vector<Region> vector;




	public RegionVector() {
		super();
		// TODO Auto-generated constructor stub
	}


	
	
	public RegionVector(String id, String geneId, Vector<Region> vector) {
		super();
		this.id = id;
		this.geneId = geneId;
		this.vector = vector;

	}




	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public Comparator<Region> getComparator() {
		return comparator;
	}




	public void setComparator(Comparator<Region> comparator) {
		this.comparator = comparator;
	}




	public int getStart() {
		
		Collections.sort(this.vector, comparator);
		return this.vector.firstElement().getStart();
		
	}
	
	
	public int getStop() {
		
		Collections.sort(this.vector, comparator);
		return this.vector.lastElement().getEnd();
	}
	
	
	public Vector<Region> merge() {
		Vector<Region> v = this.vector;
		
		Collections.sort(this.vector, comparator);
		//Utilities.printVector(vector);
		Vector<Region> merged = new Vector<Region>();	
		int tempStart=0; 
		int tempStop=0;
		
		for (Region region : v) {
			
			if(tempStart == 0) {
				tempStart = region.getStart();
				tempStop = region.getEnd();
				
			}
			else {
				
				if(tempStop>= region.getStart()) {
					//overlap
					//Region r = new Region(tempStart, region.getEnd());
					//merged.add(r);
					tempStop= region.getEnd();
				}
				else {
					Region r = new Region(tempStart, tempStop);
					merged.add(r);
					tempStart=region.getStart();
					tempStop=region.getEnd();
					
				}
			}
			
			
		}
		
		merged.add(new Region(tempStart, tempStop));
		
		return merged; 
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
