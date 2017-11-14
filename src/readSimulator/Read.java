package readSimulator;

import exonSkipping.RegionVector;

public class Read {


	private String strand;
	private int Tstart;
	private int Tstop;
	private MutatedSeq sequence;
	private RegionVector genPos;


	public Read(String strand, int tstart, int tstop, MutatedSeq sequence,
			RegionVector genPos) {
		super();
		this.strand = strand;
		Tstart = tstart;
		Tstop = tstop;
		this.sequence = sequence;
		this.genPos = genPos;
	}




	public String getStrand() {
		return strand;
	}




	public void setStrand(String strand) {
		this.strand = strand;
	}




	public int getTstart() {
		return Tstart;
	}




	public void setTstart(int tstart) {
		Tstart = tstart;
	}




	public int getTstop() {
		return Tstop;
	}




	public void setTstop(int tstop) {
		Tstop = tstop;
	}




	public MutatedSeq getSequence() {
		return sequence;
	}




	public void setSequence(MutatedSeq sequence) {
		this.sequence = sequence;
	}




	public RegionVector getGenPos() {
		return genPos;
	}




	public void setGenPos(RegionVector genPos) {
		this.genPos = genPos;
	}




	public int getLength(){
		return this.getTstop()-this.getTstart() +1 ;
	}






}
