package exonSkipping;

import java.util.HashSet;
import java.util.Set;

public class ExonSkipping {


	private Region sv;
	private RegionVector wt;
	private Set<String> svCDSids;
	private Set<String> wtCDSids;





	public ExonSkipping() {
		super();
		// TODO Auto-generated constructor stub
		this.svCDSids = new HashSet<String>();
		this.wtCDSids = new HashSet<String>();
	}



	public ExonSkipping(Region sv, RegionVector wt) {
		super();
		this.sv = sv;
		this.wt = wt;
		this.svCDSids = new HashSet<String>();
		this.wtCDSids = new HashSet<String>();
	}



	public ExonSkipping(Region sv, RegionVector wt, Set<String> svCDSids,
			Set<String> wtCDSids) {
		super();
		this.sv = sv;
		this.wt = wt;
		this.svCDSids = svCDSids;
		this.wtCDSids = wtCDSids;
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







}
