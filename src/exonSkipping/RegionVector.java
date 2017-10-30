package exonSkipping;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class RegionVector {

	private Vector<Region> vector;




	public RegionVector() {
		super();
		// TODO Auto-generated constructor stub
		this.vector = new Vector();
	}




	public RegionVector( Vector<Region> vector) {
		super();
		this.vector = vector;

	}

	public Region getFirst(){

		Collections.sort(this.vector, comparator);

		return this.vector.firstElement();


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





	public RegionVector getOnCds(RegionVector CDS){
		RegionVector rv = new RegionVector();

		Collections.sort(this.vector, comparator);
		Collections.sort(CDS.vector, comparator);

		for(Region r : this.vector){
			for(Region cds: CDS.getVector()){

					if(cds.getStart() <= r.getStart() && cds.getEnd() >= r.getEnd() ){

						rv.getVector().add(r);

					}


			}

		}

		return rv;

	}

	public RegionVector merge() {
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

				if(tempStop>= (region.getStart()-1)) {
					//overlap
					//Region r = new Region(tempStart, region.getEnd());
					//merged.add(r);
					System.out.println(region.getEnd());
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

		RegionVector mergedRv = new RegionVector(merged);
		return mergedRv;
	}




	Comparator<Region> comparator = new Comparator<Region>() {
	    @Override
	    public int compare(Region left, Region right) {
	        return left.getStart() - right.getStart(); // use your logic
	    }
	};

	 // use the comparator as much as u want



	public RegionVector inverse(){

		Vector<Region> reverse = new Vector<Region>();
		//int x1 = Runner.annotation.getGeneById(this.geneId).getStart();
		//int x3= Runner.annotation.getGeneById(this.geneId).getStart();
		Region intron;

		RegionVector merged = this.merge();
		Collections.sort(vector, comparator);
		//System.out.println(Collections.singletonList(this.vector));
		int x1= Integer.MAX_VALUE;
		this.vector.firstElement().getStart();

		//angenommen die sind sortiert
		for (Region r : merged.getVector()) {

			//nop exon at beginning
			int x2 = r.getStart();

			if(x2>x1){
				intron = new Region(x1+1,x2);
				x1= r.getEnd();
				reverse.add(intron);
			}else{

				x1= r.getEnd();
			}
			//exon at beginning



		}


		RegionVector rv = new RegionVector(reverse);

		return rv;
	}


	public RegionVector substract(RegionVector first, RegionVector second){



		return null;
	}



	public int getNumberRegion(){
		return this.vector.size();

	}
	public Vector<Region> getVector() {
		return vector;
	}


	public void setVector(Vector<Region> vector) {
		this.vector = vector;
	}








}
