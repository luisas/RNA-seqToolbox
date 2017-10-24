package exonSkipping;

public class Region {

	private int start;
	private int end;

	public Region(int start, int end) {
		super();
		this.start = start;
		this.end = end;
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
