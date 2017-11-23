package exonSkipping;

public class Region implements Comparable<Region>{

	private int start;
	private int end;

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



}
