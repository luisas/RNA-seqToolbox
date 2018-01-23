package diffSplicing;

import exonSkipping.Region;
import exonSkipping.RegionVector;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;

public class ReadPair {

	private SAMRecord fw ; 
	private SAMRecord rw ;
	
	public ReadPair(SAMRecord fw, SAMRecord rw) {
		super();
		
		if(rw.getMateNegativeStrandFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}
		
		this.fw = fw;
		this.rw = rw;
	}
	

	
	public RegionVector getExonsBoth() {
		RegionVector rv = new RegionVector();
		for(AlignmentBlock ab: this.getFw().getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()));
		}
		for(AlignmentBlock ab: this.getRw().getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()));
		}
		return rv;
		
	}
	public SAMRecord getFw() {
		return fw;
	}
	public void setFw(SAMRecord fw) {
		this.fw = fw;
	}
	public SAMRecord getRw() {
		return rw;
	}
	public void setRw(SAMRecord rw) {
		this.rw = rw;
	} 
	
	
	
	
	
}
