package exonSkipping;

import java.util.HashSet;
import java.util.Set;

public class ExonSkipping {


	private Region sv;
	private Set<RegionVector> wt;
	private Set<String> svCDSids;
	private Set<String> wtCDSids;
	private int min_skipped_exon;
	private int max_skipped_exon;
	private int min_skipped_bases;
	private int max_skipped_bases;





	public ExonSkipping() {
		super();
		// TODO Auto-generated constructor stub
		this.wt = new HashSet<RegionVector>();
		this.svCDSids = new HashSet<String>();
		this.wtCDSids = new HashSet<String>();
		min_skipped_exon = Integer.MAX_VALUE;
		max_skipped_exon = Integer.MIN_VALUE;
		min_skipped_bases = Integer.MAX_VALUE;
		max_skipped_bases = Integer.MIN_VALUE;
	}



	public ExonSkipping(Region sv, Set<RegionVector> wt) {
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



	public ExonSkipping(Region sv, Set<RegionVector> wt, Set<String> svCDSids,
			Set<String> wtCDSids) {
		super();
		this.sv = sv;
		this.wt = wt;
		this.svCDSids = svCDSids;
		this.wtCDSids = wtCDSids;
	}



	public void calcMinMax(){


		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		int min_skipped_bases= Integer.MAX_VALUE;
		int max_skipped_bases= Integer.MIN_VALUE;
		int skippedExonNr;
		int exon_length = 0;


		Region r = new Region(1723871,1730887);


		if(this.getSv().equals(r)){System.out.println("AAA");}



		for(RegionVector rv : this.getWt()){
			exon_length = 0;

			//System.out.println(Utilities.prettyRegionVector(rv));
			
			RegionVector exons = rv.inverse();
			skippedExonNr= exons.getNumberRegion();
			if(this.getSv().equals(r)){
			System.out.println("---------------------------------------------------------");
			Utilities.printRegionVector(exons);
			System.out.println("....");
			Utilities.printRegionVector(rv);
			System.out.println(exons.getNumberRegion());
			}


			if(skippedExonNr < min){
				min= skippedExonNr;

			}
			// compute max
			if(skippedExonNr > max){
				max= skippedExonNr;

			}

			for(Region exon: exons.getVector()){

				exon_length += exon.getEnd()-exon.getStart() +1;



			}

			if(exon_length<min_skipped_bases){
				min_skipped_bases = exon_length;
			}
			if(exon_length >max_skipped_bases){
				max_skipped_bases = exon_length;
			}



		}


		this.setMin_skipped_exon(min);
		this.setMax_skipped_exon(max);

		this.setMin_skipped_bases(min_skipped_bases);
		this.setMax_skipped_bases(max_skipped_bases);
	}



	public Region getSv() {
		return sv;
	}
	public void setSv(Region sv) {
		this.sv = sv;
	}



	public Set<RegionVector> getWt() {
		return wt;
	}



	public void setWt(Set<RegionVector> wt) {
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
