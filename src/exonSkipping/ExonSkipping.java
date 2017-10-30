package exonSkipping;

import java.util.HashSet;
import java.util.Set;

public class ExonSkipping {


	private Region sv;
	private RegionVector wt;
	private Set<String> svCDSids;
	private Set<String> wtCDSids;
	private int min_skipped_exon;
	private int max_skipped_exon;
	private int min_skipped_bases;
	private int max_skipped_bases;





	public ExonSkipping() {
		super();
		// TODO Auto-generated constructor stub
		this.wt = new RegionVector();
		this.svCDSids = new HashSet<String>();
		this.wtCDSids = new HashSet<String>();
		min_skipped_exon = Integer.MAX_VALUE;
		max_skipped_exon = Integer.MIN_VALUE;
		min_skipped_bases = Integer.MAX_VALUE;
		max_skipped_bases = Integer.MIN_VALUE;
	}



	public ExonSkipping(Region sv, RegionVector wt) {
		super();
		this.sv = sv;
		this.wt = wt;
		this.svCDSids = new HashSet<String>();
		this.wtCDSids = new HashSet<String>();
		min_skipped_exon = Integer.MAX_VALUE;
		max_skipped_exon = Integer.MIN_VALUE;
		min_skipped_bases = Integer.MAX_VALUE;
		max_skipped_bases = Integer.MIN_VALUE;
	}



	public ExonSkipping(Region sv, RegionVector wt, Set<String> svCDSids,
			Set<String> wtCDSids) {
		super();
		this.sv = sv;
		this.wt = wt;
		this.svCDSids = svCDSids;
		this.wtCDSids = wtCDSids;
	}



	public  void updateMinMax(){

		// compute min
		int min = this.getMin_skipped_exon();
		int max = this.getMax_skipped_exon();



		int skippedExonNr= this.getWt().getNumberRegion() -1;

		if(skippedExonNr < min){
			this.setMin_skipped_exon(skippedExonNr);
		}
		// compute max
		if(skippedExonNr > max){

			this.setMax_skipped_exon(skippedExonNr);
		}



		// compute min & max bases
		int min_skipped_bases= this.getMin_skipped_bases();
		int max_skipped_bases= this.getMax_skipped_bases();
		int exon_length;
		for(Region exon: this.getWt().inverse().getVector()){

			exon_length = exon.getEnd()-exon.getStart() +1;

			if(exon_length<min_skipped_bases){
				min_skipped_bases = exon_length;
			}
			if(exon_length >max_skipped_bases){
				max_skipped_bases = exon_length;
			}

		}
		this.setMin_skipped_bases(min_skipped_bases);
		this.setMax_skipped_bases(max_skipped_bases);
	}



	public Region getSv() {
		return sv;
	}
	public void setSv(Region sv) {
		this.sv = sv;
	}
	public RegionVector getWt() {
		return wt;
	}
	public void setWt(RegionVector wt) {
		this.wt = wt;
	}
	public Set<String> getSvCDSids() {
		return svCDSids;
	}
	public void setSvCDSids(Set<String> svCDSids) {
		this.svCDSids = svCDSids;
	}
	public Set<String> getWtCDSids() {
		return wtCDSids;
	}
	public void setWtCDSids(Set<String> wtCDSids) {
		this.wtCDSids = wtCDSids;
	}

	public int getMin_skipped_exon() {
		return min_skipped_exon;
	}

	public void setMin_skipped_exon(int min_skipped_exon) {
		this.min_skipped_exon = min_skipped_exon;
	}



	public int getMax_skipped_exon() {
		return max_skipped_exon;
	}



	public void setMax_skipped_exon(int max_skipped_exon) {
		this.max_skipped_exon = max_skipped_exon;
	}



	public int getMin_skipped_bases() {
		return min_skipped_bases;
	}



	public void setMin_skipped_bases(int min_skipped_bases) {
		this.min_skipped_bases = min_skipped_bases;
	}



	public int getMax_skipped_bases() {
		return max_skipped_bases;
	}



	public void setMax_skipped_bases(int max_skipped_bases) {
		this.max_skipped_bases = max_skipped_bases;
	}







}
