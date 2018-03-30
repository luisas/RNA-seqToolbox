package exonSkipping;

public class Region implements Comparable<Region>{

	private int start;
	private int end;

//	
//	public static void main(String[] args) {
//		Region r = new Region(2,20);
//		Region r1 = new Region(1,15);
//		
//		System.out.println(r.overlap(r1));
//	}
	public Region(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    
	    result = prime * result + start;
	    
	    result = prime * result + end;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Region r = (Region) obj;

		if(this.start ==  r.start && this.end == r.end) {
			return true;
		}else {
			return false;
		}

	}
	public boolean contains(Region r) {
		if(this.getStart()<=r.getStart() && this.getEnd() >= r.getEnd()) {
			return true;
		}else {
			return false; 
		}
	}
	//little brute force!
	public int overlap(Region r) {
		
	 int max_end = Math.max(r.getEnd(), this.getEnd()+1);
	 int min_end = Math.min(r.getEnd(), this.getEnd()+1);
	 int min_start= Math.min(r.getStart(), this.getStart());
	 int max_start= Math.max(r.getStart(), this.getStart());
	 int result = max_end-min_start-(max_end-min_end)-(max_start-min_start);
	 if(result<0) {
		 result =0;
	 }
	 return result;
		
	}
	public boolean overlapMod(Region r) {
		
		 int max_end = Math.max(r.getEnd(), this.getEnd());
		 int min_end = Math.min(r.getEnd(), this.getEnd());
		 int min_start= Math.min(r.getStart(), this.getStart());
		 int max_start= Math.max(r.getStart(), this.getStart());
		 int result = max_end-min_start-(max_end-min_end)-(max_start-min_start);
		 if(result<0) {
			 result =0;
		 }
		 return Integer.max(this.getStart(),r.getStart()) <= Integer.min(this.getEnd(),r.end);
			
	}

	public int getLength(){

		return (this.end-this.start+1);
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public int compareTo(Region o) {
		return this.getStart()-o.getStart();
	}

//	public boolean overlap(Region r) {
//		if(this.getStart() < r.getEnd() || this.getEnd() > r.getStart() ) {
//			return true;
//		}
//		
//		
//		return false; 
//	}

}
