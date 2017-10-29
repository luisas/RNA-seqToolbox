package exonSkipping;

public class Region {

	private int start;
	private int end;

	public Region(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
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

		return (this.end-this.start);
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



}
