package exonSkipping;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

	public RegionVector cut(int a , int b) {
		RegionVector result = new RegionVector();
		
		for(Region r : this.getVector()) {			
			
			if(r.getStart()<= b) {
				
				//Normal exon, which is between a and b but is just passed as it is.
				if(r.getStart()>=a && r.getEnd() <= b) {
					result.getVector().add(r);
				}
				
			    //FIRST ONE : contains a between its start and end
				else if(r.getStart()<=a && r.getEnd()>=a ) {
					//first
					if( r.getEnd() <= b) {
						
						result.getVector().add(new Region(a,r.getEnd()));
					}else {
						//Case where a and b are both inside the same region
						result.getVector().add(new Region(a,b));
						//return result;
					}

				}
				
				//LAST ONE
				else if (r.getStart()>=a  && r.getEnd() >= b) {
					
					result.getVector().add(new Region(r.getStart(),b));
				}
			
				
			}
		}
		
		
		return result; 
		
		
	}

	
	public boolean isConsistentExonSkipping(RegionVector rv, Region exon, boolean exclusive) {
		//rv is the big one 
		
		//System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
	    //System.out.println("THIS "+ Utils.prettyRegionVector(this));

		RegionVector first = new RegionVector(this.getVector());

//		System.out.println("RV "+ Utils.prettyRegionVector(first));
//		System.out.println("EXON" + exon.getStart() + " -"+ exon.getEnd());
		
//		if(!first.getVector().contains(exon)) {
//			//first.getVector().add(exon);
//			//first.getVector().sort(comparator);
//		}
		
	    RegionVector cut = rv.cut(first.getStart(),first.getStop()); 
	    //System.out.println("CUT  "+cut);
	    //cut.getNumberRegion();
	   // System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
	    if(cut.getNumberRegion()==0) {
	   		return false; 
	    }
		if(rv.cut(first.getStart(), first.getStop()).equals(first.merge())) {
			return true; 
		}
		

		return false; 
	
	}
	
	
	public static void main(String[] args) {


		
//		Region a = new Region(73024,73125);
//		Region b = new Region(72571,72579);
		Region a = new Region(57726,57733);
		Region b = new Region(57734,57828);
		
		Region c = new Region(57694,57739);	
		Region d = new Region(57740,57778);
				
		RegionVector rv1 = new RegionVector();
		RegionVector rv2 = new RegionVector();
		rv1.getVector().addElement(a);
		rv1.getVector().addElement(b);
		
		rv2.getVector().addElement(c);
		rv2.getVector().addElement(d);

		
		
		System.out.println(rv1.isConsistent(rv2));
		
		System.out.println("Nomal");
		System.out.println(Utils.prettyRegionVector(rv1));
		System.out.println(Utils.prettyRegionVector(rv2));
		Collections.sort(rv1.vector, comparator);
		System.out.println(Utils.prettyRegionVector(rv1));
		System.out.println("Merged");
		System.out.println(Utils.prettyRegionVector(rv1.merge()));

		System.out.println();
		
		
		
	}

	public boolean isConsistentMod(RegionVector rv) {
		//rv is the big one 
		
		if(this.getNumberRegion()==1 && rv.getNumberRegion()==1) {
    			return true;
		}
		
		if(this.getStop() <= rv.getStart() || rv.getStop() <= this.getStart()) {
			return true; 
		}
		
		int start = Integer.max(rv.getStart(), this.getStart());
		int stop = Integer.min(rv.getStop(), this.getStop());
		
		//System.out.println("RV "+ Utils.prettyRegionVector(rv));
		//System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
	    //System.out.println("THIS "+ Utils.prettyRegionVector(this));

		RegionVector first = new RegionVector(this.getVector());
	    RegionVector cut = rv.cut(start,stop); 
	    RegionVector cut2 = first.cut(start,stop); 

//	    System.out.println("CUT  "+Utils.prettyRegionVector(cut));
//	    System.out.println("CUT  "+Utils.prettyRegionVector(cut2));
	    //cut.getNumberRegion();
	   //System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(start,stop)));
	    
	    if(cut.getNumberRegion()==0) {
	   		return false; 
	    }

		if(cut.merge().equals(cut2.merge())) {
			return true; 
		}
		
		return false; 
	}
	
	public boolean isConsistent(RegionVector rv) {
		//rv is the big one 
		
		if(this.getNumberRegion()==1 && rv.getNumberRegion()==1) {
    			return true;
		}
		
		int start = Integer.max(rv.getStart(), this.getStart());
		int stop = Integer.min(rv.getStop(), this.getStop());
		
		//System.out.println("RV "+ Utils.prettyRegionVector(rv));
		//System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(this.getStart(), this.getStop())));
	    //System.out.println("THIS "+ Utils.prettyRegionVector(this));

		RegionVector first = new RegionVector(this.getVector());
	    RegionVector cut = rv.cut(first.getStart(),first.getStop()); 
	    //System.out.println("CUT  "+cut);
	    //cut.getNumberRegion();
	   //System.out.println("CUT "+ Utils.prettyRegionVector(rv.cut(start,stop)));
	    
	    if(cut.getNumberRegion()==0) {
	   		return false; 
	    }

		if(rv.cut(first.getStart(), first.getStop()).equals(first.merge())) {
			return true; 
		}
		
		return false; 
	}
	
	
	public boolean isTranscriptomic(RegionVector rv_read) {
		
		RegionVector transcript_rv = new RegionVector(this.getVector());
//		System.out.println("READ");
//		System.out.println(Utils.prettyRegionVector(rv_read));
//		System.out.println("TRANSCRIPT");
//		System.out.println(Utils.prettyRegionVector(transcript_rv));
//		System.out.println("INVERSED");
//		System.out.println(Utils.prettyRegionVector(transcript_rv.inverseModified()));
		
		for(Region read_region : rv_read.getVector()) {
			
			for(Region transcript_intron: transcript_rv.inverseModified().getVector()) {
				
				if(read_region.overlapMod(transcript_intron) ) {
					return false;
				}	
			}
		}
		return true; 
	}
	
	
	
	
	public boolean isTranscriptomic1(RegionVector rv_read) {
		
		//this:
		RegionVector modifiedRead = new RegionVector();
		for(Region r : rv_read.getVector()) {
			modifiedRead.getVector().addElement(new Region(r.getStart()+1, r.getEnd()));
		}
		//System.out.println("THIS "+Utils.prettyRegionVector(this.inverse()));
		for(Region r : modifiedRead.getVector()) {
			
			RegionVector intersect = this.inverse().getIntersect(r);
			if(intersect.getVector().size() > 0  && intersect.getStart()!=intersect.getStop() ) {
//				System.out.println(this.inverse().getIntersect(r).getVector().get(0).getStart());
				return false; 
			}
		}	
		
		return true; 
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
		   
		   if(rv.getVector().size()!= this.getVector().size()) {
			   return false;
		   }
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
		Vector<Region> merged = new Vector<Region>();
		int tempStart=0;
		int tempStop=0;
		
		for (Region region : v) {

			//first run, initialization
			if(tempStart == 0) {
				tempStart = region.getStart();
				tempStop = region.getEnd();
				continue;
			}
			// Normal runs
			else {
				
				if(tempStop>= (region.getStart()-1)) {
					//overlap
					//Region r = new Region(tempStart, region.getEnd());
					//merged.add(r);
					//System.out.println(region.getEnd());
					if(!(tempStop >= region.getEnd())) {
						tempStop= region.getEnd();
					}
					
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

	public RegionVector mergeSpec() {
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
	    	
	    		if(left.getStart() == right.getStart()) {
	    			return left.getEnd() - right.getEnd();
	    		}
	        return left.getStart() - right.getStart(); 
	    }
	};

	
	
	public boolean overlap(RegionVector rv) {
		
		Region one = new Region(this.getStart(), this.getStop());
		Region two = new Region(rv.getStart(), rv.getStop());
		
		if(one.getStart()<=two.getStart() && one.getEnd() >= two.getStart()) {
			return true;
		}
		if(two.getStart()<=one.getStart() && two.getEnd() >= one.getStart()) {
			return true;
		}
		
		
		
		
		return false; 
		
	}

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

	public RegionVector inverseModified(){

		Vector<Region> reverse = new Vector<Region>();

		Region intron;

		RegionVector merged = this.merge();
		Collections.sort(vector, comparator);

		int x1= Integer.MAX_VALUE;
		this.vector.firstElement().getStart();

		for (Region r :this.getVector()) {


			int x2 = r.getStart();

			if(x2>x1){
				//HERE ONLY MOD
				intron = new Region(x1+1,x2-1);
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

