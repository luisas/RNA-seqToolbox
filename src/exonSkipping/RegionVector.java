package exonSkipping;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import readSimulator.Utils;

public class RegionVector {

	private Vector<Region> vector;



	public RegionVector() {
		super();
		this.vector = new Vector<Region>();
	}


	public RegionVector( Vector<Region> vector) {
		super();
		this.vector = vector;

	}


	public Region getElement(int i){

		Collections.sort(this.vector);

		return vector.get(i);

	}

	public Region getFirst(){

		Collections.sort(this.vector, comparator);

		return this.vector.firstElement();

	}

	public RegionVector getIntersect(Region r){

	RegionVector rv = new RegionVector();

	Collections.sort(this.getVector());


      for (Region region : this.getVector()){
    	  //System.out.println(region.getStart()+"-"+region.getEnd());


    	  //CASE 1: one region in the region vector is completely contained in the r Region.
    	  if(region.getStart() >= r.getStart() && region.getEnd()<= r.getEnd()){

    		  System.out.println(region.getEnd());
    		  System.out.println(r.getEnd());
    		  if(region.getEnd() == r.getEnd()){
    			  //System.out.println("here");
    			  rv.getVector().add(new Region(region.getStart(),region.getEnd()));
    		  }else{
        		  rv.getVector().add(new Region(region.getStart(),region.getEnd()+1));

    		  }
    		  //System.out.println("1");
    		  continue;
    	  }
    	  //CASE 2: one region in the region vector is bigger than the R (r is contained in it)
    	  else if (region.getStart() <= r.getStart() && region.getEnd()>= r.getEnd()){
    		  rv=new RegionVector();
    		  rv.getVector().add(r);
    		  //System.out.println("2");
    		  break;
    	  }
    	  //CASE 3: one region
    	  else if (region.getStart()<=r.getStart()  && region.getEnd() <= r.getEnd() && region.getEnd()+1 >=r.getStart()){
    		  rv=new RegionVector();
    		  rv.getVector().add(new Region(r.getStart(),region.getEnd()+1));
    		  //System.out.println("3");
    		  continue;

    	  }
    	  else if(region.getStart() >= r.getStart() && region.getEnd()>= r.getEnd() &&  region.getStart() < r.getEnd()){

    		  //System.out.println("4");
    		  rv.getVector().add(new Region(region.getStart(),r.getEnd()));
    		  break ;
    	  }


      }


		return rv;


	}

	public Region getLast(){

		Collections.sort(this.vector, comparator);

		return this.vector.lastElement();

	}



	public int getRegionsLength(){


		int tot = 0 ;
		for(Region r : this.getVector()){
			tot+=r.getLength();
		}
		return tot;

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
					//System.out.println(region.getEnd());
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




	static Comparator<Region> comparator = new Comparator<Region>() {
	    @Override
	    public int compare(Region left, Region right) {
	        return left.getStart() - right.getStart(); // use your logic
	    }
	};


	public RegionVector inverse(boolean verbose){

		Vector<Region> reverse = new Vector<Region>();

		Region intron;

		//RegionVector merged = this.merge();
		Collections.sort(vector, comparator);

		int x1= this.vector.firstElement().getStart();


		for (Region r : this.getVector()) {


			int x2 = r.getStart();


			if(x2>x1){

				intron = new Region(x1+1,x2);
				x1= r.getEnd();
				reverse.add(intron);
			}else{

				x1= r.getEnd();
			}

		}

		RegionVector rv = new RegionVector(reverse);
		return rv;
	}



	public RegionVector inverse(){

		Vector<Region> reverse = new Vector<Region>();

		Region intron;

		RegionVector merged = this.merge();
		Collections.sort(vector, comparator);

		int x1= Integer.MAX_VALUE;
		this.vector.firstElement().getStart();

		for (Region r :this.getVector()) {


			int x2 = r.getStart();

			if(x2>x1){
				intron = new Region(x1+1,x2);
				x1= r.getEnd();
				reverse.add(intron);
			}else{

				x1= r.getEnd();
			}

		}

		RegionVector rv = new RegionVector(reverse);
		return rv;
	}


	//TODO
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

