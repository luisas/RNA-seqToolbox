package exonSkipping;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import augmentedTree.IntervalTree;
import readSimulator.Utils;

public class RegionVector {

	private Vector<Region> vector;


	public RegionVector getAllLeftExons(Region exon) {
		
		RegionVector result = new RegionVector();
		int i = 0 ; 
		
		int indexExon = -1 ; 
		for(Region r : this.getVector()) {
			if(r.equals(exon)) {
				indexExon = i; 
			}
			i++;
		}
		for( int j = 0 ; j< indexExon ; j ++ ) {
			
			result.getVector().add(this.getElement(j));
		}
			
		return result; 	
		
	}
	
	public RegionVector getAllRigthExons(Region exon) {
		RegionVector result = new RegionVector();
		int i = 0 ; 
		
		int indexExon = -1 ; 
		for(Region r : this.getVector()) {
			if(r.equals(exon)) {
				indexExon = i; 
			}
			i++;
		}
		for( int j = indexExon ; j< this.getVector().size() ; j ++ ) {
			
			result.getVector().add(this.getElement(j));
		}
			
		return result; 	
	}
	
	
	

	public Region getLeftExon(Region exon) {
		
		int i = 0 ; 
		
		int indexExon = -1 ; 
		for(Region r : this.getVector()) {
			if(r.equals(exon)) {
				indexExon = i; 
			}
			i++;
		}
		
		
		return this.vector.get(indexExon -1);
		
		
	}
	
	
	public Region getRightExon(Region exon) {
		
		HashMap<Integer, Region> map = new HashMap<Integer, Region>();
		int i = 0 ; 
		
		int indexExon = -1 ; 
		for(Region r : this.getVector()) {
			if(r.equals(exon)) {
				indexExon = i; 
			}
			i++;
		}
		
		
		return this.vector.get(indexExon +1 );
		
		
	}
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

	public RegionVector cut2(int a , int b) {
		RegionVector result = new RegionVector();
		
		for(Region r : this.getVector()) {
			
			if(r.getStart()< b) {
			if(r.getStart()>=a && r.getEnd() <= b) {
				result.getVector().add(r);
			}
			//FIRST ONE
			else if(r.getStart()<=a && r.getEnd()>=a ) {
//				System.out.println("-----------------------------");
//				System.out.println(r.getStart()+ " "+r.getEnd());
//				System.out.println("-----------------------------");

				//first
				if( r.getEnd() <= b) {
					result.getVector().add(new Region(a,r.getEnd()));
				}else {
					result.getVector().add(new Region(a,b));
				}

			}
			else if (r.getStart()>=a  && r.getEnd() >= b) {
				result.getVector().add(new Region(r.getStart(),b));
			}
			}
		}
		
		
		return result; 
		
		
	}
	
	public RegionVector cut(int a , int b) {
		RegionVector result = new RegionVector();
		
		for(Region r : this.getVector()) {
			if(r.getStart()>=a && r.getEnd() <= b) {
				result.getVector().add(r);
			}
			//FIRST ONE
			else if(r.getStart()<=a && r.getEnd()>=a ) {
//				System.out.println("-----------------------------");
//				System.out.println(r.getStart()+ " "+r.getEnd());
//				System.out.println("-----------------------------");

				//first
				if( r.getEnd() <= b) {
					result.getVector().add(new Region(a,r.getEnd()));
				}else {
					result.getVector().add(new Region(a,b));
				}

			}
			else if (r.getStart()>=a  && r.getEnd() >= b) {
				result.getVector().add(new Region(r.getStart(),b));
			}
		}
		
		
		return result; 
		
		
	}
	
	public boolean isConsistent(RegionVector rv) {
		//rv is the big one 
		
		//System.out.println("RV "+ Utils.prettyRegionVector(rv));
		//System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
	    //System.out.println("THIS "+ Utils.prettyRegionVector(this));

	    RegionVector cut = rv.cut(this.getStart(), this.getStop()); 
	    //System.out.println("CUT  "+cut);
	    //cut.getNumberRegion();
	   // System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
    if(cut.getNumberRegion()==0) {
    		//System.out.println("-----------------------------------------");
   		return false; 
    }
		if(rv.cut(this.getStart(), this.getStop()).equals(this.merge())) {
			return true; 
		}
		

	return false; 
	
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


	
	 public boolean isMergedContained(RegionVector rv) {
		   
		 boolean regionContained = false; 
		 for(Region r : this.getVector()) {
			 regionContained =false;
			 for(Region big : rv.getVector()) {
				 if(r.getStart() >= big.getStart() && r.getEnd() <= big.getEnd()) {
					 regionContained=true; 
				 }
			 }
			 if(!regionContained) {
				 return false; 
			 }
		 }
		 
		 return true; 
		   
	 }
	 
	 
		@Override
		public int hashCode() {
		    final int prime = 31;
		    int result = 1;
		    for(Region r : this.getVector()) {
		    	 result = prime * result + r.getStart();
		    	 result = prime * result + r.getEnd();
		    }
		    
		    return result;
		}
	
	   @Override
	public boolean equals(Object o) {
		   
		// TODO Auto-generated method stub
		   boolean got = false; 
		   RegionVector rv = (RegionVector ) o;
		   
		   int size = rv.getVector().size();
		   
		   
		   for (int i = 0 ; i < size ; i ++) {
			   Region r = rv.getVector().get(i);
			   Region v = this.getVector().get(i);
			   if(v.getStart() !=  r.getStart() || v.getEnd() !=r.getEnd()) {
				   return false;
			   }
		   }
		   return true; 
		   
//		  for(Region r : this.getVector()) {
//			  got = false; 
//			  for(Region v : rv.getVector()) {
//				  if(v.getStart() !=  r.getStart() || v.getEnd() !=r.getEnd()) {
//					  System.out.println(v.getStart()+ "  "  +r.getStart());
//					  System.out.println(v.getEnd()+ "  "  +r.getEnd());
//					  //got = true;
//					  return false; 
//				  }
//			  }
////			  if(!got) {
////				  return false; 
////			  }
//		  }
//		   
//		  //System.out.println(Utils.prettyRegionVector(rv) +"-----------" + Utils.prettyRegionVector(this )); 
//		return true;
	}


	 public boolean contains(Region r) {
		 for(Region region : this.getVector()) {
			 if(region.equals(r)) {
				 return true;
			 }
		 }
		 return false; 
	 }
	public boolean contained(RegionVector rv) {
		   
		   //rv big one
		   
		   for(Region r : this.getVector()) {
			   if(!rv.getVector().contains(r)) {
				   return false; 
			   }
		   }
		   return true; 
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

